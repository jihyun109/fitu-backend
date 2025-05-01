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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
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

        if (userEntity == null) {
            isNewUser = true;
            userEntity = createNewUser(kakaoProfile);
        }

        Long userId = userEntity.getId();
        String accessToken = jwtUtil.createAccessToken(userId, userEntity.getRole());
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
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED.getMessage(), ErrorCode.EMPTY_FILE);
        }

        Long userId = jwtUtil.getUserId(refreshToken);
        Role role = userRepository.findById(userId).get().getRole();

        String newAccessToken = jwtUtil.createAccessToken(userId, role);
        return TokenResponseDTO.builder()
                .token(newAccessToken)
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
