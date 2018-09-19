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
     * @param coures
     * @return
     */
    int insert(Course coures);
}
