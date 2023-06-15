package com.econovation.idp;


import com.econovation.idpcommon.IdpCommonApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/** 스프링 부트 설정의 컴포넌트 스캔범위를 지정 통합 테스트를 위함 */
@Configuration
@ComponentScan(
        basePackageClasses = {IdpInfrastructureApplication.class, IdpCommonApplication.class})
public class InfraIntegrateTestConfig {}
