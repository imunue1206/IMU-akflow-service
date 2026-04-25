package com.imu.akflow;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages = "com.imu.akflow")
@MapperScan("com.imu.akflow.mapper")
public class AkflowApplication {

    public static void main(String[] args) {
        SpringApplication.run(AkflowApplication.class, args);
    }
}
