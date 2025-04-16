package com.hsp.fitu.config;

import com.hsp.fitu.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    public static final String[] ALLOW_URLS = {
            "/v3/api-docs/**",
            "/api/v1/posts/**",
            "/api/v1/replies/**",
            "/login",
            "/auth/login/kakao/**",
//            "/physical-infos/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CSRF 설정 Disable
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(ALLOW_URLS).permitAll()  // /login, /signup 경로는 인증 없이 접근 가능
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, BasicAuthenticationFilter.class)
                .cors(withDefaults())

        ;  // 그 외 모든 요청은 인증된 사용자만 접근 가능

        return http.build();  // 필터 체인 빌드
    }
}
