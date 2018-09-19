package com.hfut.issf.grade.dao;

import com.hfut.issf.grade.domain.Student;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StudentDao {
    /**查询所有学生
     * @return List student
     */
    List<Student> selectAll();

    /** 根据Id查询学生
     * @param id id
     * @return student
     */
    Student selectById(int id);

    /** 根据用户id查询学生
     * @param id user id
     * @return student
     */
    Student selectByUserId(int id);

    /** 插入学生
     * @param student
     * @return
     */
    int insert(Student student);
}