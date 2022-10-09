package com.swagger.docs.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swagger.docs.exception.ErrorResult;
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
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.PrintWriter;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper objectMapper;
    private final String UNAUTHORIZEd_CUSTOM_MESSAGE = "넌 accessHandler에서 걸렸어 임마";

/*
    private final AccessDeniedHandler accessDeniedHandler =
            (request, response, accessDeniedException) -> {
                ErrorResult fail = new ErrorResult("401", "fail했다 임마");
                response.setStatus(HttpStatus.FORBIDDEN.value());
                String json = objectMapper.writeValueAsString(fail);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                PrintWriter writer = response.getWriter();
                writer.write(json);
                writer.flush();
            };
*/

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.antMatcher("/**")
                .authorizeRequests()
//                .antMatchers("/api/v1/**").hasAuthority(USER.name())
                .and()
//                .httpBasic().disable()
                .cors().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                특정 URL 차단 및 접근권한 설정
                .and()
                .authorizeRequests()// 시큐리티 처리에 HttpServeltRequest를 사용합니다.
                .antMatchers("/api/user/**").hasAuthority("USER")
                .antMatchers("/api/account/**").permitAll()
//                ErrorHandling 처리
                .and()
//                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)  //JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 전에 넣는다
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)  //JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 전에 넣는다
                .exceptionHandling()
//                .accessDeniedHandler(accessDeniedHandler)
                //Exception Handler ( 예외 발생 시, UNAUTHORIZED 처리 )
                .authenticationEntryPoint(((request, response, authException) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    PrintWriter writer = response.getWriter();
                    String json = objectMapper.writeValueAsString(UNAUTHORIZEd_CUSTOM_MESSAGE);
                    writer.write(json);
                    writer.flush();

                    objectMapper.writeValue(
                            response.getOutputStream(),
                            new BasicResponse("exception event",HttpStatus.FORBIDDEN)
                    );
                })).and().build();
//                .build();
    }
}