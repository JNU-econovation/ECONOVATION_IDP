package com.econovation.idpapi;


import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.web.filter.ForwardedHeaderFilter;

@RequiredArgsConstructor
@ComponentScan(basePackages = {"com.econovation"})
@SpringBootApplication
@Slf4j
public class IdpApiApplication implements ApplicationListener<ApplicationReadyEvent> {
    private final Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(IdpApiApplication.class, args);
    }
    @Bean
    ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("applicationReady status" + Arrays.toString(environment.getActiveProfiles()));
    }
}
