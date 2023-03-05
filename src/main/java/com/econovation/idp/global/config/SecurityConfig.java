package com.econovation.idp.global.config;//package com.econovation.idp.global.config;
//
//import com.econovation.idp.application.service.UserService;
//import com.econovation.idp.global.config.jwt.JwtAuthenticationFilter;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//@EnableWebSecurity
//@Configuration
//@RequiredArgsConstructor
//public class SecurityConfig {
//    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//    private final ObjectMapper objectMapper;
//
//    private final UserService userService;
//
//    private final String UNAUTHORIZEd_CUSTOM_MESSAGE = "인증받지 못한 유저입니다. 로그인을 재시도해주세요.";
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        return http.csrf().disable()
//            .cors().disable()
//                .authorizeHttpRequests((requests) -> {
//                    try {
//                        requests
////                                .requestMatchers("/swagger-ui.html").permitAll()
////                                .requestMatchers("/v3/api-docs").permitAll()
////                                .requestMatchers("/swagger-resources").permitAll()
////                                .requestMatchers("/swagger-resources/**").permitAll()
////                                .and()
//                            .requestMatchers("/**").permitAll()
////                            .requestMatchers("/api/user/**").permitAll()
////                            .requestMatchers("/api/account/**").permitAll()
//                            .and()
//        //                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)  //JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 전에 넣는다
//                            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)  //JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 전에 넣는다
//                            .rememberMe()
//                            .key("secret-key")
//                            .alwaysRemember(true)
//                            .tokenValiditySeconds(86400 * 30)
//                            .userDetailsService(userService);
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                    }
//                }).exceptionHandling()
////                    .authenticationSuccessHandler())
//            //Exception Handler ( 예외 발생 시, UNAUTHORIZED 처리 )
//            .authenticationEntryPoint(((request, response, authException) -> {
//                response.setStatus(HttpStatus.UNAUTHORIZED.value());
//                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//            })).and()
//        .build();
//    }
////                .antMatchers("/api/v1/**").hasAuthority(USER.name())
////                특정 URL 차단 및 접근권한 설정
////                ErrorHandling 처리
////                .accessDeniedHandler(accessDeniedHandler)
//
//
//}


//---------------------------------------------

import com.econovation.idp.global.config.jwt.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;

@EnableWebSecurity
//@AllArgsConstructor
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper objectMapper;

    private final String UNAUTHORIZEd_CUSTOM_MESSAGE = "인증받지 못한 유저입니다. 로그인을 재시도해주세요.";

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.httpFirewall(defaultHttpFirewall());
    }

    @Bean
    public HttpFirewall defaultHttpFirewall() {
        return new DefaultHttpFirewall();
    }


    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/**")
                .authorizeRequests()
//                .antMatchers("/api/v1/**").hasAuthority(USER.name())
                .and()
                .cors().disable()
                .csrf().disable()
                .httpBasic().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                특정 URL 차단 및 접근권한 설정
                .and()
                .authorizeRequests()// 시큐리티 처리에 HttpServeltRequest를 사용합니다.
                .antMatchers("/**").permitAll()
                .antMatchers("/non/**").permitAll()
                .antMatchers("/api/users/**").hasAuthority("USER")
                .antMatchers("/api/accounts/**").permitAll()
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
//                    PrintWriter writer = response.getWriter();
//                    String json = objectMapper.writeValueAsString(UNAUTHORIZEd_CUSTOM_MESSAGE);
//                    writer.write(json);
//                    writer.flush();
//
//                    objectMapper.writeValue(
//                            response.getOutputStream(),
//                            new BasicResponse("exception event",HttpStatus.FORBIDDEN)
//                    );
                }));
    }

}