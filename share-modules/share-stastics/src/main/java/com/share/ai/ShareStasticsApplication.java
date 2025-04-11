package com.share.ai;

import com.share.common.security.annotation.EnableCustomConfig;
import com.share.common.security.annotation.EnableRyFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@EnableCustomConfig
@EnableRyFeignClients
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class ShareStasticsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShareStasticsApplication.class, args);
    }

}
