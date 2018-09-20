package com.hfut.issf.grade.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hfut.issf.grade.domain.Course;
import com.hfut.issf.grade.domain.Student;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.parser.Entity;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class Crawler {
    private List<NameValuePair> nameValuePairList;
    List<Header> headerList;
    private String[] cells = {"courseName", "courseNum", "classNum", "credit", "gpa", "grade"};
    private String[] semesters = {"32", "33"};

    public Crawler() {
        //初始化部分查询参数
        nameValuePairList = new ArrayList<>();
        nameValuePairList.add(new BasicNameValuePair("IDToken0", ""));
        nameValuePairList.add(new BasicNameValuePair("IDButton", "Submit"));
        nameValuePairList.add(new BasicNameValuePair("goto","aHR0cDovL2p4Z2xzdHUuaGZ1dC5lZHUuY246ODAvZWFtczUtc3R1ZGVudC93aXNjb20tc3NvL2xvZ2lu"));
        nameValuePairList.add(new BasicNameValuePair("encoded", "true"));
        nameValuePairList.add(new BasicNameValuePair("inputCode", ""));
        nameValuePairList.add(new BasicNameValuePair("gx_charset", "UTF-8"));

        //默认头
        List<Header> headerList = new ArrayList<>();
        headerList.add(new BasicHeader(HttpHeaders.ACCEPT_ENCODING,"gzip, deflate"));
        headerList.add(new BasicHeader(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"));
        headerList.add(new BasicHeader(HttpHeaders.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.9"));
        headerList.add(new BasicHeader(HttpHeaders.CONNECTION, "keep-alive"));
        headerList.add(new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded"));
        headerList.add(new BasicHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.92 Safari/537.36"));


    }


    /** 获取登录后的授权cookie
     * @param username 用户名
     * @param password 密码
     * @return cookieStore
     * @throws URISyntaxException
     */
    private CookieStore getCookieStore(String username, String password) throws URISyntaxException {
        nameValuePairList.add(new BasicNameValuePair("IDToken1", username));
        nameValuePairList.add(new BasicNameValuePair("IDToken2", password));

        String loginUrl = "http://ids1.hfut.edu.cn/amserver/UI/Login";
        URI loginUri = new URIBuilder(loginUrl).addParameters(nameValuePairList).build();
        HttpUriRequest loginRequest = RequestBuilder.get().setUri(loginUri).build();
        HttpClientContext context = HttpClientContext.create();
        HttpClient httpClient = HttpClients.custom().setDefaultHeaders(headerList).build();
        try {
            httpClient.execute(loginRequest, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return context.getCookieStore();
    }

    /** 获取成绩
     * @param cookieStore cookie
     * @return
     */
    private Map<String,List> getGrade(CookieStore cookieStore){
        Map<String, List> gradeMap = null;
        try {
            HttpClient httpClient = HttpClientBuilder.create().setDefaultHeaders(headerList).setDefaultCookieStore(cookieStore).build();
            String gradeUrl = "http://jxglstu.hfut.edu.cn/eams5-student/for-std/grade/sheet";
            HttpClientContext context = HttpClientContext.create();
            HttpGet httpGet = new HttpGet(gradeUrl);
            httpClient.execute(httpGet, context);
            String redirectUri = context.getRedirectLocations().get(0).toString();
            //获取Id
            String id = redirectUri.substring(redirectUri.lastIndexOf('/') + 1, redirectUri.length());
            //构造查询URL
            String allGradeUrl = "http://jxglstu.hfut.edu.cn/eams5-student/for-std/grade/sheet/info/";
            String lastUrl = allGradeUrl + id + "?semester=";
            //重新创建httpClient，并使用登陆后的cookie，原因是使用原来的httpClient进行处理会卡在这里，原因不明。。。
            HttpClient client2 = HttpClientBuilder
                    .create()
                    .setDefaultCookieStore(context.getCookieStore())
                    .build();
            HttpResponse response = client2.execute(new HttpGet(lastUrl));
            String rawHtml = EntityUtils.toString(response.getEntity());
            //将HTML文档转换成Jsoup文档进行搜索
            Document doc = Jsoup.parse(rawHtml);
            EntityUtils.consume(response.getEntity());
            //获取所有学期table的父节点div
            Elements semesterList = doc.select("body > div > div");
            gradeMap = new HashMap<>();
            for (int i = 0; i < 2;i++) {
                //h3为学期文本
                Element element = semesterList.get(i);
                String semester = element.selectFirst("div > h3").text();
                log.info(semester);
                //获取table中tbody的每一行
                Elements courseList = element.select("div:has(table) > table > tbody > tr");
                List<Map> courses = new ArrayList<>();
                for (Element course : courseList) {
                    Map<String, String> courseMap = new HashMap<>();
                    Elements tds = course.select("td");
                    for (int j = 0; j < 6; j++) {
                        courseMap.put(cells[j], tds.get(j).text());
                    }
                    courses.add(courseMap);
                }
                gradeMap.put(semester, courses);
            }
        } catch (IOException  e) {
            e.printStackTrace();
        } finally {
            return gradeMap;
        }

    }

    private Student getStudentInfo(CookieStore cookieStore) {
        Student student = null;
        try {
            HttpClient client = HttpClients.custom()
                    .setDefaultCookieStore(cookieStore)
                    .setDefaultHeaders(headerList)
                    .build();
            String infoUrl = "http://jxglstu.hfut.edu.cn/eams5-student/for-std/student-info";
            HttpGet get = new HttpGet(infoUrl);
            HttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
            //获取Html文档
            String rawHtml = EntityUtils.toString(entity);
            //转换 Jsoup document
            Document document = Jsoup.parse(rawHtml);
            EntityUtils.consume(entity);
            //学好
            String stuNum = document.select("#base-info > div > div.col-xs-12.col-sm-4 > ul > li:nth-child(3) > span:nth-child(2)").text();
            //姓名
            String name = document.select("#base-info > div > div.col-xs-12.col-sm-4 > ul > li:nth-child(4) > span:nth-child(2)").text();
            //性别
            String sex = document.select("#base-info > div > div.col-xs-12.col-sm-4 > ul > li:nth-child(6) > span:nth-child(2)").text();
            //年级
            String grade = document.select("#base-info > div > div.col-xs-12.col-sm-8 > div > dl > dd:nth-child(6)").text();
            //专业
            String major = document.select("#base-info > div > div.col-xs-12.col-sm-8 > div > dl > dd:nth-child(14)").text();
            //班级
            String clazz = document.select("#base-info > div > div.col-xs-12.col-sm-8 > div > dl > dd:nth-child(18)").text();
            student = new Student();
            student.setClassName(clazz);
            student.setGrade(grade);
            student.setName(name);
            student.setNum(stuNum);
            student.setMajor(major);
            student.setSex(sex);
            return student;

        } catch ( IOException e) {
            e.printStackTrace();
        }
        return student;
    }

    private List<String> getCourseJson(CookieStore cookieStore) {
        List<String> jsonList = null;
        try {
            jsonList = new ArrayList<>();
            HttpClient client = HttpClients.custom().setDefaultHeaders(headerList).setDefaultCookieStore(cookieStore).build();
            String tableUrl = "http://jxglstu.hfut.edu.cn/eams5-student/for-std/course-table";
            HttpGet tableGet = new HttpGet(tableUrl);
            HttpClientContext context = HttpClientContext.create();
            HttpResponse response = client.execute(tableGet, context);
            String reditectUrl = context.getRedirectLocations().get(0).toString();
            String id = reditectUrl.substring(reditectUrl.lastIndexOf('/') + 1, reditectUrl.length());
            String queryUrl = "http://jxglstu.hfut.edu.cn/eams5-student/for-std/course-table/get-data";
            List<NameValuePair> pairs = new ArrayList<>();
            pairs.add(new BasicNameValuePair("bizTypeId", "2"));
            pairs.add(new BasicNameValuePair("dataId", id));
            for (String s : semesters) {
                if (pairs.size() > 2) {
                    pairs.remove(2);
                }
                pairs.add(new BasicNameValuePair("semesterId", s));
                URI queryURI = new URIBuilder(queryUrl).addParameters(pairs).build();
                HttpUriRequest queryRequest = RequestBuilder.get().setUri(queryURI).build();
                HttpResponse queryResponse = client.execute(queryRequest);
                HttpEntity queryEntity = queryResponse.getEntity();
                String json = EntityUtils.toString(queryEntity);
                jsonList.add(json);
            }
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        return jsonList;
    }

    private List<Course> getCourse(CookieStore cookieStore) {
        List<Course> courseList = new ArrayList<>();
        try {
            List<String> jsonList = getCourseJson(cookieStore);
            ObjectMapper mapper = new ObjectMapper();
            for (String json : jsonList) {
                Map map = new HashMap<>();
                map = mapper.readValue(json, Map.class);
                List lessons = (List) map.get("lessons");
                for (Object lesson1 : lessons) {
                    Course course = new Course();
                    Map lesson = (Map) lesson1;
                    Map courseMap = (Map) lesson.get("course");
                    course.setName(courseMap.get("nameZh").toString());
                    course.setNum(courseMap.get("code").toString());
                    course.setCredit(courseMap.get("credits").toString());
                    Map courseType = (Map) lesson.get("courseType");
                    course.setType(courseType.get("nameZh").toString());
                    courseList.add(course);
                    Map department = (Map) lesson.get("openDepartment");
                    course.setDepartment(department.get("nameZh").toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return courseList;
    }

    public Map<String, Object> crawler(String username, String password) {
        CookieStore cookieStore = null;
        try {
            cookieStore = getCookieStore(username, password);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Map<String, Object> map = new HashMap<>();
        map.put("stuInfo", getStudentInfo(cookieStore));
        map.put("grade", getGrade(cookieStore));
        map.put("course", getCourse(cookieStore));
        return map;
    }
}
