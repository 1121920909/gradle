package com.hfut.issf.grade.domain;

import lombok.Data;

@Data
public class Course {
    int id;
    String name;
    String num;
    String type;
    String module;
    String credit;
    String startSemester;
    String suggestSemester;
    boolean isRequired;
    String department;
}
