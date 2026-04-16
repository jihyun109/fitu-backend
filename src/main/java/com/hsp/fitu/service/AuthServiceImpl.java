package com.hsp.fitu.service;

import com.hsp.fitu.dto.KakaoDTO;
import com.hsp.fitu.dto.LoginDTO;
import com.hsp.fitu.dto.TokenResponseDTO;
import com.hsp.fitu.entity.UserEntity;
import com.hsp.fitu.entity.enums.Role;
import com.hsp.fitu.error.BusinessException;
import com.hsp.fitu.error.ErrorCode;
import com.hsp.fitu.jwt.JwtUtil;
import com.hsp.fitu.jwt.RefreshTokenStore;
import com.hsp.fitu.repository.UserRepository;
import com.hsp.fitu.util.KakaoUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final KakaoUtil kakaoUtil;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RefreshTokenStore refreshTokenStore;

    @Value("${jwt.token.refresh-expiration-time}")
    private Long refreshExpMs;

    @Value("${app.cookie.secure:false}")
    private boolean cookieSecure;

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

        // RT 발급: familyId와 jti를 생성하여 Redis에 등록
        String familyId = UUID.randomUUID().toString();
        String jti = UUID.randomUUID().toString();
        String refreshToken = jwtUtil.createRefreshToken(userId, familyId, jti);
        refreshTokenStore.issue(userId, familyId, jti, refreshExpMs);

        httpServletResponse.setHeader("Authorization", "Bearer " + accessToken);
        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, buildRefreshCookie(refreshToken).toString());

        return LoginDTO.builder()
                .isNewUser(isNewUser)
                .token(accessToken)
                .userId(userId)
                .build();
    }

    @Override
    public TokenResponseDTO reissue(String refreshToken, HttpServletResponse response) {
        // 1. RT 검증 (refreshKey로 서명/만료 확인 + type=REFRESH assertion)
        Claims claims = jwtUtil.parseRefreshClaims(refreshToken);
        String jti = jwtUtil.getJti(claims);
        String familyId = jwtUtil.getFamilyId(claims);
        Long userId = jwtUtil.getUserId(claims);

        // 2. 레거시 토큰 거부 (jti/fid 없는 구버전)
        if (jti == null || familyId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        // 3. 원자적 consume: 기존 RT를 소비하고 결과에 따라 분기
        String newJti = UUID.randomUUID().toString();
        RefreshTokenStore.ConsumeOutcome outcome = refreshTokenStore.consume(jti, familyId, newJti, refreshExpMs);

        switch (outcome) {
            case OK -> { /* 정상 — 계속 진행 */ }
            case NOT_FOUND, FAMILY_REVOKED -> throw new BusinessException(ErrorCode.UNAUTHORIZED);
            case REUSE_DETECTED -> {
                // 탈취 감지: 가족 전체 revoke 후 거부
                log.warn("REFRESH_TOKEN_REUSE_DETECTED userId={} familyId={} consumedJti={}", userId, familyId, jti);
                refreshTokenStore.revokeFamily(familyId, refreshExpMs);
                throw new BusinessException(ErrorCode.REFRESH_TOKEN_REUSED);
            }
        }

        // 4. 새 RT 발급 및 Redis 등록
        refreshTokenStore.issue(userId, familyId, newJti, refreshExpMs);
        String newRefreshToken = jwtUtil.createRefreshToken(userId, familyId, newJti);

        // 5. 새 AT 발급
        UserEntity userEntity = userRepository.findById(userId).orElse(null);
        Role role = null;
        Long universityId = null;
        if (userEntity != null) {
            role = userEntity.getRole();
            universityId = userEntity.getUniversityId();
        }
        String newAccessToken = jwtUtil.createAccessToken(userId, role, universityId);

        // 6. 새 RT를 쿠키로 설정
        response.addHeader(HttpHeaders.SET_COOKIE, buildRefreshCookie(newRefreshToken).toString());

        return TokenResponseDTO.builder()
                .token(newAccessToken)
                .build();
    }

    @Override
    public void logout(String refreshToken, HttpServletResponse response) {
        // RT가 없어도 쿠키 삭제 (멱등성)
        if (refreshToken != null) {
            try {
                Claims claims = jwtUtil.parseRefreshClaims(refreshToken);
                String familyId = jwtUtil.getFamilyId(claims);
                if (familyId != null) {
                    refreshTokenStore.revokeFamily(familyId, refreshExpMs);
                }
            } catch (BusinessException ignored) {
                // 만료/잘못된 토큰이어도 쿠키는 삭제
            }
        }
        // 쿠키 삭제
        response.addHeader(HttpHeaders.SET_COOKIE, clearRefreshCookie().toString());
    }

    /**
     * refresh token 쿠키 빌더. login/reissue에서 재사용.
     */
    private ResponseCookie buildRefreshCookie(String token) {
        return ResponseCookie.from("refreshToken", token)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(refreshExpMs / 1000)
                .sameSite("Lax")
                .build();
    }

    /**
     * refresh token 쿠키 삭제용 빌더. logout에서 사용.
     */
    private ResponseCookie clearRefreshCookie() {
        return ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
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