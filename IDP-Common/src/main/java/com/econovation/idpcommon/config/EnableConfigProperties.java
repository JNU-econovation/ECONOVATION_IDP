package com.econovation.idpcommon.config;


import com.econovation.idpcommon.properties.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties({JwtProperties.class})
@Configuration
public class EnableConfigProperties {}
