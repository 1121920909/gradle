package com.hfut.issf.grade.controller.form;

import lombok.Data;

@Data
public class GradeQuery {
    private String courseName = "";
    private String courseNum = "";
    private String courseType = "";
    private String semester = "";
}
