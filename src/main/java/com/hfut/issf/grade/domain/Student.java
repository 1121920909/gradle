package com.hfut.issf.grade.domain;

import lombok.Data;

@Data
public class Student {
    int id;
    int userId;
    int num;
    String name;
    String sex;
    String grade;
    String major;
    String className;
}
