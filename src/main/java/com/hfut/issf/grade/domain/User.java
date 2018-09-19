package com.hfut.issf.grade.domain;

import lombok.Data;

import java.util.List;

@Data
public class User {
    int id;
    String userName;
    String password;
    List<String> roles;
}
