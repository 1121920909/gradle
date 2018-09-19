package com.hfut.issf.grade.dao;

import com.hfut.issf.grade.domain.CourseChoosing;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CourseChoosingDao {
    /** 添加成绩
     * @param courseChoosing
     * @return
     */
    int insert(CourseChoosing courseChoosing);
}
