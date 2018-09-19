package com.hfut.issf.grade.domain;

import lombok.Data;

@Data
public class Grade {
    int studentNum;
    String courseName;
    String credit;
    String courseType;
    boolean courseRequired;
    String className;
    String semester;
    int grade;
    double gpa;
}
