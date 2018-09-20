package com.hfut.issf.grade.controller.form;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
public class StatisticsQuery {
    private String[] semesters;
    private String[] courseType;
}
