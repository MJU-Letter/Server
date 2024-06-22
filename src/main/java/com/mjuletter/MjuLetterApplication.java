package com.mjuletter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import io.github.cdimascio.dotenv.Dotenv;

@EnableJpaAuditing // Jpa Auditing 기능(생성일, 수정일 자동 생성 기능)을 사용할 수 있도록 추가
@SpringBootApplication
public class MjuLetterApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        System.setProperty("db.host", dotenv.get("DB_HOST"));
        System.setProperty("db.name", dotenv.get("DB_NAME"));
        System.setProperty("db.port", dotenv.get("DB_PORT"));
        System.setProperty("db.user", dotenv.get("DB_USERNAME"));
        System.setProperty("db.password", dotenv.get("DB_PASSWORD"));
        SpringApplication.run(MjuLetterApplication.class, args);
    }

}

