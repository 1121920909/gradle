package com.hfut.issf.grade.domain;

import lombok.Data;

@Data
public class CourseChoosing {
    int id;
    int studentId;
    int classesId;
    int grade;
    double gpa;
}
