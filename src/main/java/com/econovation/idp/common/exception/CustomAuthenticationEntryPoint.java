package com.econovation.idp.global.common.exception;


import com.econovation.idp.global.common.BasicResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Getter
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException, ServletException {
        String exception = (String) request.getAttribute("exception");
        setResponse(response);
        BasicResponse basicResponse = new BasicResponse(exception, HttpStatus.FORBIDDEN);
        response.getWriter().print(convertObjectToJson(basicResponse));
    }

    //    HttpResponse
    public void setResponse(HttpServletResponse response) {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }

    private String convertObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        return mapper.writeValueAsString(object);
    }
}
