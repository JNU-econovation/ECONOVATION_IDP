package com.econovation.idp.global.config;//package com.econovation.idp.global.config;

import com.econovation.idp.global.config.jwt.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
//@AllArgsConstructor
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper objectMapper;
    private final String UNAUTHORIZEd_CUSTOM_MESSAGE = "인증받지 못한 유저입니다. 로그인을 재시도해주세요.";
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.antMatcher("/**")
                .authorizeRequests()
//                .antMatchers("/api/v1/**").hasAuthority(USER.name())
                .and()
                    .cors()
                .and()
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
                    .antMatchers(HttpMethod.OPTIONS,"/api/accounts/**").permitAll()
                    .antMatchers(HttpMethod.GET,"/api/accounts/**").permitAll()
                    .antMatchers(HttpMethod.POST,"/api/accounts/**").permitAll()
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
                }));
    }
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

//        configuration.setAllowedOrigins(Arrays.asList("http://auth.econovation.kr"));
        configuration.setAllowedOriginPatterns(Arrays.asList("http://auth.econovation.kr","http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("HEAD","POST","GET","DELETE","PUT","OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization", "access-control-allow-credentials", "access-control-allow-origin", "REQUEST_URL"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(Duration.ofDays(30));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
