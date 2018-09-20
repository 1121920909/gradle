package com.hfut.issf.grade.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class StudentServiceTest {
    @Autowired
    private StudentService studentService;

    @Test
    public void crawlerTest() {
        studentService.crawlerGrade("2015213453","hezixi0122","2015213453");
    }
}
