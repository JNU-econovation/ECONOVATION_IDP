package com.econovation.idpcommon.properties;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@AllArgsConstructor
@Component
@NoArgsConstructor
// @ConstructorBinding
// @ConfigurationProperties(prefix = "auth.jwt")
public class JwtProperties {
    @Value("${auth.jwt.secret}")
    private String secretKey;

    @Value("${auth.jwt.access}")
    private Long accessExp;

    @Value("${auth.jwt.refresh}")
    private Long refreshExp;
}
