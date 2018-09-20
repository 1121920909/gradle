package com.hfut.issf.grade.service;

import com.hfut.issf.grade.dao.*;
import com.hfut.issf.grade.domain.*;
import com.hfut.issf.grade.util.Crawler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class StudentService {
    private final Crawler crawler;
    private final ClassesDao classesDao;
    private final CourseDao courseDao;
    private final StudentDao studentDao;
    private final CourseChoosingDao choosingDao;
    private final UserDao userDao;
    private final GradeDao gradeDao;

    @Autowired
    public StudentService(Crawler crawler,
                          ClassesDao classesDao,
                          CourseDao courseDao,
                          StudentDao studentDao,
                          CourseChoosingDao choosingDao,
                          UserDao userDao,
                          GradeDao gradeDao) {
        this.crawler = crawler;
        this.classesDao = classesDao;
        this.courseDao = courseDao;
        this.studentDao = studentDao;
        this.choosingDao = choosingDao;
        this.userDao = userDao;
        this.gradeDao = gradeDao;
    }

    /** 查询学生课程成绩
     * @param stuNum 学号
     * @param courseName 课程名称
     * @param courseType 课程类别
     * @param isRequired 是否必修
     * @param className 教学班
     * @param semester 学期
     * @return List
     */
    public List<Grade> getGrades(String stuNum,
                                String courseName,
                                String courseType,
                                int isRequired,
                                String className,
                                String semester,
                                String courseNum) {
        return gradeDao.selectGrade(stuNum,
                courseName,
                courseType,
                isRequired,
                className,
                semester,
                courseNum);
    }

    /** 查询用户成绩
     * @param username 用户名
     * @return student
     */
    public Student getStudentInfo(String username) {
        return studentDao.selectByUserId(userDao.selectByUserName(username).getId());
    }

    /** 从教务系统爬去用户成绩信息
     * @param stuNum 学号
     * @param password 密码
     * @param userName 用户名
     */
    public void crawlerGrade(String stuNum, String password, String userName) {
        Map map = crawler.crawler(userName, password);
        Student student = (Student) map.get("stuInfo");
        Map<String,List> semesterMap =(Map) map.get("grade");
        List<Course> courseList = (List<Course>) map.get("course");
        //获取登陆用户id
        int userId = userDao.selectByUserName(userName).getId();
        //如果该用户已经绑定了一个student就更新student否则新增一个
        student.setUserId(userId);
        Student pristine = studentDao.selectByUserId(userId);
        if (pristine == null) {
            studentDao.insert(student);
        }else{
            studentDao.update(student);
        }
        //将所选课程中没有的添加到数据库
        for (Course course : courseList) {
            //获取课程代码的最后一位，如果是B为必修，否则为选修
            char lastChar = course.getNum().toCharArray()[course.getNum().length() - 1];
            if (lastChar == 'B') {
                course.setRequired(true);
            }else {
                course.setRequired(false);
            }
            if(courseDao.selectByNum(course.getNum()) == null){
                courseDao.insert(course);
            }else{
                courseDao.update(course);
            }

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
                    classes = new Classes();
                    classes.setCourseId(course.getId());
                    classes.setName(classNum);
                    classes.setSemester(semester);
                    classesDao.insert(classes);
                }
                CourseChoosing choosing = new CourseChoosing();
                choosing.setClassesId(classes.getId());
                choosing.setGpa(Double.parseDouble(courseMap.get("gpa")));
                choosing.setGrade(courseMap.get("grade"));
                choosing.setStudentId(studentDao.selectByUserId(userDao.selectByUserName(userName).getId()).getId());
                if (choosingDao.selectByStuIdAndClassId(choosing.getStudentId(), choosing.getClassesId()) == null) {
                    choosingDao.insert(choosing);
                }else{
                    choosingDao.update(choosing);
                }
            }
        }
    }


    /** 获取所有课程类型
     * @return
     */
    public List<String> getALlCourseType() {
        return courseDao.selectAllType();
    }

    /** 获取所有学期
     * @return
     */
    public List<String> getAllSemester() {
        return classesDao.selectAllSemester();
    }
}
