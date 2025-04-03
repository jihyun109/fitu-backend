package com.hsp.fitu.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CorsConfig corsConfig;
    public static final String[] ALLOW_URLS = {
            "/v3/api-docs/**",
            "/api/v1/posts/**",
            "/api/v1/replies/**",
            "/login",
            "/auth/login/kakao"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CSRF 설정 Disable
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(ALLOW_URLS).permitAll()  // /login, /signup 경로는 인증 없이 접근 가능
                        .anyRequest().authenticated())
//                .cors(withDefaults())
                .cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource())); // ← 이 부분 변경

        ;  // 그 외 모든 요청은 인증된 사용자만 접근 가능

        return http.build();  // 필터 체인 빌드
    }

//    //    커스텀 필터 추가를 여기서 처리하기
//    public class MyCustomDsl extends AbstractHttpConfigurer<MyCustomDsl, HttpSecurity> {
//        @Override
//        public void configure(HttpSecurity http) throws Exception {
//            AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
//            http
////            cors 오류를 해결하기 위해 Controller 에 @CrossOrigin 을 붙여주는 방법도 있지만
////				이 방식은 필터 추가와 다르게 인증이 필요 없는 url 만 처리해줌
//                    .addFilter(corsConfig.corsFilter()) // cors 에 대해 허락하는 필터
//                    .addFilter(new JwtAuthorizationFilter(authenticationManager));
//        }
//    }
}
