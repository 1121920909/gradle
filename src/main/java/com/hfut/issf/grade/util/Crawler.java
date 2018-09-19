package com.hfut.issf.grade.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
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
import org.springframework.stereotype.Repository;

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
    private String[] cells = {"courseName", "courseNum", "classNum", "credit", "gpa", "grade"};
    private HttpClient httpClient;

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

        httpClient = HttpClients.custom().setDefaultHeaders(headerList).build();
    }

    public Map<String,List> getGrade(String username, String password) throws URISyntaxException {
        //设置登陆用户名和密码
        nameValuePairList.add(new BasicNameValuePair("IDToken1", username));
        nameValuePairList.add(new BasicNameValuePair("IDToken2", password));

        String loginUrl = "http://ids1.hfut.edu.cn/amserver/UI/Login";
        URI loginUri = new URIBuilder(loginUrl).addParameters(nameValuePairList).build();
        HttpUriRequest loginRequest = RequestBuilder.get().setUri(loginUri).build();
        try {
            httpClient.execute(loginRequest);
            HttpClientContext context = HttpClientContext.create();
            String gradeUrl = "http://jxglstu.hfut.edu.cn/eams5-student/for-std/grade/sheet";
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
            Map<String, List> gradeMap = new HashMap<>();
            for (Element element : semesterList) {
                //h3为学期文本
                String semester = element.selectFirst("div > h3").text();
                log.info(semester);
                //获取table中tbody的每一行
                Elements courseList = element.select("div:has(table) > table > tbody > tr");
                List<Map> courses = new ArrayList<>();
                for (Element course : courseList) {
                    Map<String, String> courseMap = new HashMap<>();
                    Elements tds = course.select("td");
                    for (int i = 0; i < 6; i++) {
                        courseMap.put(cells[i], tds.get(i).text());
                    }
                    courses.add(courseMap);
                }
                gradeMap.put(semester, courses);
            }
            log.info("map:{}",gradeMap);
            return gradeMap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
