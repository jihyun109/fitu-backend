package com.hsp.fitu.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final HandlerExceptionResolver resolver;

    public CustomAuthenticationEntryPoint(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        // 필터에서 저장한 구체적인 예외가 있다면 꺼내고, 없으면 기본 authException 사용
        Exception exception = (Exception) request.getAttribute("exception");
        if (exception == null) exception = authException;

        // 예외 처리를 GlobalExceptionHandler로 배달!
        resolver.resolveException(request, response, null, exception);
    }
}
