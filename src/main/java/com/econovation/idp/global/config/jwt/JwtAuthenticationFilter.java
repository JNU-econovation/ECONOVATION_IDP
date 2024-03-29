package com.econovation.idp.global.config.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request.getHeader("Authorization"));
        boolean isLogin = false;
        if (token != null ) {
            Authentication authentication = jwtProvider.validateToken(request, token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            isLogin = true;
        }
        if(isLogin){
            filterChain.doFilter(request, response);
        }
//        로그인 하지 않은 사람의 처치
        else{
//            일반 요청이라면
            if(request.getRequestURI().startsWith("/api/accounts") || request.getRequestURI().startsWith("non/api/") ||
                    request.getRequestURI().startsWith("/swagger") || request.getRequestURI().startsWith("/api-docs")){
                filterChain.doFilter(request,response);
                return;
            }
//            유저 조회 요청이면
            log.info("유저 조회 요청입니다.");
            RequestDispatcher rd = request.getRequestDispatcher("/non" + request.getRequestURI());
            rd.forward(request, response);
        }
        log.info(request.getRequestURI().toString());
    }

    private String resolveToken(String authorization) {
        return authorization != null ? authorization.substring(7) : null;
    }
}