package com.hfut.issf.grade;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.hfut.issf.grade.dao")
public class GradleApplication {
    public static void main(String[] args) {
        SpringApplication.run(GradleApplication.class, args);
    }
}
