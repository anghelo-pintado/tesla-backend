package com.tesla;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Este escudo bloquea a DevTools y a Spring Cloud para que no busquen Web MVC ni Bases de Datos
@SpringBootApplication(excludeName = {
        "org.springframework.cloud.autoconfigure.LifecycleMvcEndpointAutoConfiguration",
        "org.springframework.cloud.autoconfigure.RefreshAutoConfiguration",
        "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration",
        "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration"
})
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}