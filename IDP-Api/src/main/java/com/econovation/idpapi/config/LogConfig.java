package com.econovation.idpapi.config;


import com.econovation.idp.global.common.auth.AuthComponent;
import com.econovation.idp.global.common.auth.LogInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class LogConfig implements WebMvcConfigurer {
    private final AuthComponent authComponent;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor(authComponent));
    }
}
