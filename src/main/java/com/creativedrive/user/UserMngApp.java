package com.creativedrive.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication(scanBasePackages = {
        "com.creativedrive.user",
})
public class UserMngApp {

    public static void main(String[] args) throws Exception {
        // Make application work with UTC dates
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(UserMngApp.class, args);
    }

}
