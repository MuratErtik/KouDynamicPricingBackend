package org.example.koudynamicpricingbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@EnableAsync
public class KoudynamicpricingbackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(KoudynamicpricingbackendApplication.class, args);
    }

}
