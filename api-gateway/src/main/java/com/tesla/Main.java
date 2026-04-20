package com.tesla;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // Esta es la "llave" que enciende todos los motores de Spring
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}