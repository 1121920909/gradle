package com.hfut.issf.grade.dao;

import com.hfut.issf.grade.domain.Grade;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GradeDao {
    /** 查询所有成绩
     * @return
     */
    List<Grade> selectAll();

    /** 根据条件查询成绩
     * @param stuNum 学号
     * @param courseName 课名
     * @param courseType 课程类型
     * @param isRequired 是否必修
     * @param className 教学班
     * @param semester 学期
     * @return
     */
    List<Grade> selectGrade(@Param("stuNum") String stuNum,
                            @Param("courseName") String courseName,
                            @Param("courseType") String courseType,
                            @Param("isRequired") int isRequired,
                            @Param("className") String className,
                            @Param("semester") String semester);
}
