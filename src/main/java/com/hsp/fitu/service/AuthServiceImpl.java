package com.hsp.fitu.service;

import com.hsp.fitu.dto.KakaoDTO;
import com.hsp.fitu.entity.UserEntity;
import com.hsp.fitu.jwt.JwtUtil;
import com.hsp.fitu.repository.UserRepository;
import com.hsp.fitu.util.KakaoUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final KakaoUtil kakaoUtil;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    public void oAuthLogin(String accessCode, HttpServletResponse httpServletResponse) {
        KakaoDTO.OAuthToken oAuthToken = kakaoUtil.requestToken(accessCode);
        KakaoDTO.KakaoProfile kakaoProfile = kakaoUtil.requestProfile(oAuthToken);
        String kakaoEmail = kakaoProfile.getKakao_account().getEmail();

        UserEntity userEntity = userRepository.findByKakaoEmail(kakaoEmail)
                .orElseGet(() -> createNewUser(kakaoProfile));

        String token = jwtUtil.createAccessToken(userEntity.getKakaoEmail());
        httpServletResponse.setHeader("Authorization", "Bearer " + token);
    }

    private UserEntity createNewUser(KakaoDTO.KakaoProfile kakaoProfile) {
        UserEntity newUser = UserEntity.builder()
                .kakaoEmail(kakaoProfile.getKakao_account().getEmail())
                .build();
        userRepository.save(newUser);
        return newUser;
    }
}
