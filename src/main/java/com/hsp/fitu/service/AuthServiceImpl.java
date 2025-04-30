package com.hsp.fitu.service;

import com.hsp.fitu.dto.KakaoDTO;
import com.hsp.fitu.dto.LoginDTO;
import com.hsp.fitu.entity.UserEntity;
import com.hsp.fitu.jwt.JwtUtil;
import com.hsp.fitu.repository.UserRepository;
import com.hsp.fitu.util.KakaoUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final KakaoUtil kakaoUtil;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Override
    public LoginDTO oAuthLogin(String accessCode, HttpServletResponse httpServletResponse) {
        boolean isNewUser = false;
        log.info("현재 적용된 DB URL: " + dbUrl);
        KakaoDTO.OAuthToken oAuthToken = kakaoUtil.requestToken(accessCode);
        KakaoDTO.KakaoProfile kakaoProfile = kakaoUtil.requestProfile(oAuthToken);
        String kakaoEmail = kakaoProfile.getKakao_account().getEmail();

        UserEntity userEntity = userRepository.findByKakaoEmail(kakaoEmail)
                .orElse(null);
//                .orElseGet(() -> createNewUser(kakaoProfile));

        if (userEntity == null) {
            isNewUser = true;
            log.warn("hi");
            userEntity = createNewUser(kakaoProfile);
        }

        String token = jwtUtil.createAccessToken(userEntity.getId());
        httpServletResponse.setHeader("Authorization", "Bearer " + token);

        return LoginDTO.builder()
                .isNewUser(isNewUser)
                .token(token)
                .build();
    }

    private UserEntity createNewUser(KakaoDTO.KakaoProfile kakaoProfile) {
        UserEntity newUser = UserEntity.builder()
                .kakaoEmail(kakaoProfile.getKakao_account().getEmail())
                .build();
        userRepository.save(newUser);
        return newUser;
    }
}
