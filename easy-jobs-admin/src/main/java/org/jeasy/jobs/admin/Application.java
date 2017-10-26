package org.jeasy.jobs.admin;

import org.jeasy.jobs.ContextConfiguration;
import org.jeasy.jobs.admin.web.WebContextConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@EnableAutoConfiguration(exclude = HibernateJpaAutoConfiguration.class)
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(
                new Object[]{Application.class, ContextConfiguration.class, WebContextConfiguration.class},
                args
        );
    }

}