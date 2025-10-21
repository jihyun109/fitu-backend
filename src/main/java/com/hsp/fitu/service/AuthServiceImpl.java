package com.hsp.fitu.service;

import com.hsp.fitu.dto.KakaoDTO;
import com.hsp.fitu.dto.LoginDTO;
import com.hsp.fitu.dto.TokenResponseDTO;
import com.hsp.fitu.entity.UserEntity;
import com.hsp.fitu.entity.enums.Role;
import com.hsp.fitu.error.ErrorCode;
import com.hsp.fitu.error.customExceptions.UnauthorizedException;
import com.hsp.fitu.jwt.JwtUtil;
import com.hsp.fitu.repository.UserRepository;
import com.hsp.fitu.util.KakaoUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final KakaoUtil kakaoUtil;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Value("${jwt.token.refresh-expiration-time}")
    private Long refreshExpMs;

    @Override
    public LoginDTO oAuthLogin(String accessCode, HttpServletResponse httpServletResponse) {
        boolean isNewUser = false;
        KakaoDTO.OAuthToken oAuthToken = kakaoUtil.requestToken(accessCode);
        KakaoDTO.KakaoProfile kakaoProfile = kakaoUtil.requestProfile(oAuthToken);
        String kakaoEmail = kakaoProfile.getKakao_account().getEmail();

        UserEntity userEntity = userRepository.findByKakaoEmail(kakaoEmail)
                .orElse(null);
        Role role;

        // new user 인 경우
        if (userEntity == null) {
            isNewUser = true;
            userEntity = createNewUser(kakaoProfile);
            role = Role.USER;
        } else {
            role = userEntity.getRole();
        }

        Long userId = userEntity.getId();
        Long universityId = userEntity.getUniversityId();

        String accessToken = jwtUtil.createAccessToken(userId, role, universityId);
        String refreshToken = jwtUtil.createRefreshToken(userId);
        httpServletResponse.setHeader("Authorization", "Bearer " + accessToken);

        // refresh accessToken 쿠키에 저장
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(refreshExpMs)
                .sameSite("None")
                .build();
        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return LoginDTO.builder()
                .isNewUser(isNewUser)
                .token(accessToken)
                .build();
    }

    @Override
    public TokenResponseDTO reissue(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new UnauthorizedException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        Long userId = jwtUtil.getUserId(refreshToken);
        UserEntity userEntity = userRepository.findById(userId).orElse(null);

        Role role = null;
        Long universityId = null;
        if (userEntity != null) {
            role = userEntity.getRole();
            universityId = userEntity.getUniversityId();
        }

        String newAccessToken = jwtUtil.createAccessToken(userId, role, universityId);
        return TokenResponseDTO.builder()
                .token(newAccessToken)
                .build();
    }

    private UserEntity createNewUser(KakaoDTO.KakaoProfile kakaoProfile) {
        String kakaoEmail = kakaoProfile.getKakao_account().getEmail();
        UserEntity newUser = UserEntity.builder()
                .kakaoEmail(kakaoEmail)
                .build();
        userRepository.save(newUser);
        return userRepository.findByKakaoEmail(kakaoEmail).orElse(null);
    }
}
