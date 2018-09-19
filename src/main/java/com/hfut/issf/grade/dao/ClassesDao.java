package com.hfut.issf.grade.dao;

import com.hfut.issf.grade.domain.Classes;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ClassesDao {
    /**
     * 查找所有教学班
     *
     * @return List
     */
    List<Classes> selectAllClasses();

    /**
     * 根据id查询教学班
     * @param id id
     * @return classes
     */
    Classes selectById(int id);

    /**
     * 是否存在教学班
     * @param num 教学班号
     * @return 0-不存在，1-存在
     */
    int hasNum(String num);

    /** 根据教学班号查询教学班
     * @param num 教学班号
     * @return Classes
     */
    Classes selectByNum(String num);

    int insert(Classes classes);
}
