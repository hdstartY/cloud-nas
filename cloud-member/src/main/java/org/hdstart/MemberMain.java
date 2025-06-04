package org.hdstart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
//@EnableDiscoveryClient
public class MemberMain {
    public static void main(String[] args) {
        SpringApplication.run(MemberMain.class,args);
    }
}