package org.hdstart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableDiscoveryClient
@EnableFeignClients
@EnableAsync
//@EnableElasticsearchRepositories(basePackages = "org.hdstart.cloud.esmapper")
public class MemberMain {
    public static void main(String[] args) {
        SpringApplication.run(MemberMain.class,args);
    }
}