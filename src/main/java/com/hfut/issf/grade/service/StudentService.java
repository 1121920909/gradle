package com.hfut.issf.grade.service;

import com.hfut.issf.grade.dao.*;
import com.hfut.issf.grade.domain.Classes;
import com.hfut.issf.grade.domain.Course;
import com.hfut.issf.grade.domain.CourseChoosing;
import com.hfut.issf.grade.util.Crawler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class StudentService {
    private final Crawler crawler;
    private final ClassesDao classesDao;
    private final CourseDao courseDao;
    private final StudentDao studentDao;
    private final CourseChoosingDao choosingDao;
    private final UserDao userDao;

    @Autowired
    public StudentService(Crawler crawler,
                          ClassesDao classesDao,
                          CourseDao courseDao,
                          StudentDao studentDao,
                          CourseChoosingDao choosingDao,
                          UserDao userDao) {
        this.crawler = crawler;
        this.classesDao = classesDao;
        this.courseDao = courseDao;
        this.studentDao = studentDao;
        this.choosingDao = choosingDao;
        this.userDao = userDao;
    }

    /** 从教务系统爬去用户成绩信息
     * @param stuNum 学号
     * @param password 密码
     * @param userName 用户名
     */
    public void crawlerGrade(String stuNum, String password, String userName) {
        Map<String,List> semesterMap = null;
        try {
            semesterMap = crawler.getGrade(stuNum, password);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        if (semesterMap != null) {
            //TODO 抛出一个异常
        }
        //所有学期的集合
        Set<String> semesterSet = semesterMap.keySet();
        for (String semester : semesterSet) {
            //一个学期课程成绩集合
            List<Map<String, String>> semesterList = semesterMap.get(semester);
            for (Map<String, String> courseMap : semesterList) {
                //教学班代码
                String classNum = courseMap.get("classNum");
                Classes classes = classesDao.selectByNum(classNum);
                //如果没有这个教学班，就向数据库中新增这个教学班
                if (classes == null) {
                    String courseNum = courseMap.get("courseNum");
                    Course course = courseDao.selectByNum(courseNum);
                    if (course == null) {
                        //TODO 抛出异常
                    }
                    classes = new Classes();
                    classes.setCourseId(course.getId());
                    classes.setName(courseNum);
                    classes.setSemester(semester);
                    classesDao.insert(classes);
                }
                CourseChoosing choosing = new CourseChoosing();
                choosing.setClassesId(classes.getId());
                choosing.setGpa(Double.parseDouble(courseMap.get("gpa")));
                choosing.setGrade(Integer.parseInt(courseMap.get("grade")));
                choosing.setStudentId(studentDao.selectByUserId(userDao.selectByUserName(userName).getId()).getId());
                choosingDao.insert(choosing);
            }
        }
    }


}
