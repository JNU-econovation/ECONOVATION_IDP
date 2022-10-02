package com.swagger.docs.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swagger.docs.global.common.BasicResponse;
import com.swagger.docs.global.config.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.antMatcher("/**")
                .authorizeRequests()
//                .antMatchers("/api/v1/**").hasAuthority(USER.name())
                .and()
                .httpBasic().disable()
                .cors().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()// 시큐리티 처리에 HttpServeltRequest를 사용합니다.
                .anyRequest().permitAll()
                .and()
//                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)  //JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 전에 넣는다
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)  //JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 전에 넣는다
                .exceptionHandling()    //Exception Handler
                .authenticationEntryPoint(((request, response, authException) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    objectMapper.writeValue(
                            response.getOutputStream(),
                            new BasicResponse("exception event",HttpStatus.FORBIDDEN)
                    );
                })).and().build();
//                AccessDenied Handler


    }
//
//    @Bean
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .csrf().disable()  //  security login 해제
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 토큰 기반 인증이므로 세션 X
//                .and()
//                .authorizeRequests()
//                .antMatchers("/join", "/login", "/user/signup", "/user/login", "/exception/**", "/configuration/**")
//                .permitAll()
//                .antMatchers("/swagger/api-docs", "/swagger-ui*/**", "/webjars/**")
//                .permitAll()
////                .antMatchers("/admin/**").hasRole("ADMIN")
////                .antMatchers("/user/**").hasRole("USER")
////                .anyRequest().authenticated()
//                .and()
//                .addFilterBefore(jwtAuthenticationFilter,
//                        UsernamePasswordAuthenticationFilter.class);
//
//    }
}