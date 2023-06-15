package com.econovation.idpdomain.config;


import com.econovation.idp.config.slack.config.SlackAsyncErrorSender;
import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
    private final SlackAsyncErrorSender slackAsyncErrorSender;

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        log.error("Exception message - " + ex);
        log.error("Method Name : " + method.getName());
        for (Object param : params) {
            log.error("Parameter value - " + param);
        }
        slackAsyncErrorSender.execute(method.getName(), ex, params);
    }
}
