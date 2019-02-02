package com.repomgr.repomanager;

import com.repomgr.repomanager.config.RepoManagerProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RepoManagerProperties.class)
public class RepoManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RepoManagerApplication.class, args);
    }

}

