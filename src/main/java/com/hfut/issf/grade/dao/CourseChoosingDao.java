package com.hfut.issf.grade.dao;

import com.hfut.issf.grade.domain.CourseChoosing;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CourseChoosingDao {
    /** 添加成绩
     * @param courseChoosing
     * @return
     */
    int insert(CourseChoosing courseChoosing);

    /** 根据学生Id和教学班Id查询
     * @param stuId 学生Id
     * @param classId 教学班 Id
     * @return
     */
    CourseChoosing selectByStuIdAndClassId(@Param("stuId") int stuId, @Param("classId") int classId);

    int update(CourseChoosing courseChoosing);
}
