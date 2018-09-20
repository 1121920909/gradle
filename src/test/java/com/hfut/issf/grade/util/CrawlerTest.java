package com.hfut.issf.grade.util;

import com.hfut.issf.grade.domain.Course;
import com.hfut.issf.grade.domain.Student;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class CrawlerTest {
    @Autowired
    Crawler crawler;

    @Test
    public void crawlerTest() {
        Map map = crawler.crawler("2015213518", "z13457669888");
        log.info("map: {}",map);
    }

}
