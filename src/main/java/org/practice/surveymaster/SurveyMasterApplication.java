package org.practice.surveymaster;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@MapperScan("org.practice.surveymaster.mapper")
@EnableCaching
public class SurveyMasterApplication {

    public static void main(String[] args) {
        SpringApplication.run(SurveyMasterApplication.class, args);
    }

}
