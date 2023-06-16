package com.econovation.idpcommon.properties;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.stereotype.Component;

@Getter
@AllArgsConstructor
@Component
@NoArgsConstructor
//@ConstructorBinding
//@ConfigurationProperties(prefix = "auth.jwt")
public class JwtProperties {
    @Value("${auth.jwt.secret}")
    private String secretKey;
    @Value("${auth.jwt.access}")
    private String accessExp;
    @Value("${auth.jwt.refresh}")
    private String refreshExp;
}
