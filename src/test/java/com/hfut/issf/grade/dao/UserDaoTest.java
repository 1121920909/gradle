package com.hfut.issf.grade.dao;

import com.hfut.issf.grade.domain.Student;
import com.hfut.issf.grade.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class UserDaoTest {
    @Autowired
    UserDao dao;

    @Test
    public void selectAll() {
        List<User> list = dao.selectAll();
        log.info("student list: {}",list);
    }
}
