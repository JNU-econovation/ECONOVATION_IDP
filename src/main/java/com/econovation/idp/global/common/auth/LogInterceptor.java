package com.econovation.idp.global.common.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogInterceptor implements HandlerInterceptor {

    private final AuthComponent authComponent;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        Auth auth = handlerMethod.getMethodAnnotation(Auth.class);
        if (auth == null) {
            return true;
        }
//        비회원인지 인증
        if (auth.role().compareTo(Auth.Role.GUEST) == 0) {
            return true;
        }

        if (auth.role().compareTo(Auth.Role.USER) == 0) {
            authComponent.validateUser(request);
        }
        // 그룹에 속해있는 멤버인지 확인 (관리자 or 일반 멤버)
        if (auth.role().compareTo(Auth.Role.ADMIN) == 0) {
            authComponent.validateAdmin(request);
        }
        return true;
    }

}
