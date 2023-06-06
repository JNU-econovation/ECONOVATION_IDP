package com.econovation.idp.global.common.config.security;


import com.econovation.idp.global.common.config.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FilterConfig
        extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    //    private final JwtTokenFilter jwtTokenFilter;
    private final AccessDeniedFilter accessDeniedFilter;
    //    private final JwtExceptionFilter jwtExceptionFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Override
    public void configure(HttpSecurity builder) {
        builder.addFilterBefore(jwtAuthenticationFilter, BasicAuthenticationFilter.class);
        //        builder.addFilterBefore(jwtTokenFilter, BasicAuthenticationFilter.class);
        //        builder.addFilterBefore(jwtExceptionFilter, JwtTokenFilter.class);
        builder.addFilterBefore(accessDeniedFilter, FilterSecurityInterceptor.class);
    }
}
