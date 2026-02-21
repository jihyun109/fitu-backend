package com.hsp.fitu.config;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;

public final class SecurityConstants {
    // 인스턴스화 방지
    private SecurityConstants() {
        throw new AssertionError("Constants class should not be instantiated.");
    }

    // 허용 경로 리스트
    public static final List<RequestMatcher> PERMIT_ALL_MATCHERS = List.of(
            // 인증 관련
            new AntPathRequestMatcher("/login", "POST"),
            new AntPathRequestMatcher("/signup", "POST"),
            new AntPathRequestMatcher("/auth/login/kakao/**"),
            new AntPathRequestMatcher("/auth/reissue"),

            // API 및 데이터 관련
            new AntPathRequestMatcher("/v3/api-docs/**"),

            // 도구 및 모니터링
            new AntPathRequestMatcher("/swagger-ui/**"),
            new AntPathRequestMatcher("/swagger-ui.html"),
            new AntPathRequestMatcher("/actuator/health/**"),
            new AntPathRequestMatcher("/actuator/**"),
            new AntPathRequestMatcher("/ws/**")
    );
}