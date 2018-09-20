package com.hfut.issf.grade.dao;

import com.hfut.issf.grade.domain.Course;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CourseDao {
    /** 查询所有课程
     * @return List Course
     */
    List<Course> selectAll();

    /** 根据id查询课程
     * @param id id
     * @return Course
     */
    Course selectById(int id);

    /**
     * 根据课程代码查询课程
     * @param num 课程代码
     * @return Course
     */
    Course selectByNum(String num);


    /**插入课程
     * @param course
     * @return
     */
    int insert(Course course);

    /** 更新课程信息
     * @param course
     * @return
     */
    int update(Course course);

    /** 获取所有课程类型
     * @return string list
     */
    List<String> selectAllType();
}
