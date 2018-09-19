package com.hfut.issf.grade.dao;

import com.hfut.issf.grade.domain.Classes;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ClassesDaoTest {
    @Autowired
    ClassesDao classesDao;

    @Test
    public void getAllClasses() {
        List<Classes> classes= classesDao.selectAllClasses();
        log.info("classes: {}",classes);
    }

    @Test
    public void selectByNum() {
        Classes classes = classesDao.selectByNum("123");
        log.info("class: {}",classes);
    }

    @Test
    public void insert() {
        Classes classes = new Classes();
        classes.setCourseId(1);
        classes.setName("200000");
        classes.setSemester("test");
        int id = classesDao.insert(classes);
        log.info("id:{}\treally id:{}",id,classes.getId());
    }
}
