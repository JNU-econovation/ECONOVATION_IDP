package com.econovation.idpapi.supports;


import com.econovation.idp.IdpInfrastructureApplication;
import com.econovation.idpapi.IdpApiApplication;
import com.econovation.idpcommon.IdpCommonApplication;
import com.econovation.idpdomain.IdpDomainApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/** 스프링 부트 설정의 컴포넌트 스캔범위를 지정 통합 테스트를 위함 */
@Configuration
@ComponentScan(
        basePackageClasses = {
            IdpApiApplication.class,
            IdpCommonApplication.class,
            IdpInfrastructureApplication.class,
            IdpDomainApplication.class
        })
public class ApiIntegrateTestConfig {}
