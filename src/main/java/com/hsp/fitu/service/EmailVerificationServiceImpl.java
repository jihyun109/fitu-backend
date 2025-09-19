package com.hsp.fitu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {
    private final JavaMailSender mailSender;
    private final RedisTemplate<String, String> redisTemplate;

    private static final Duration CODE_TTL = Duration.ofMinutes(5);
    private static final int CODE_LEN = 6;

    @Override
    public void sendVerificationCode(String email) {
        // 1) 코드 생성 및 저장
        String code = generateCode(CODE_LEN);
        String key = "email:verify:" + email;
        redisTemplate.opsForValue().set(key, code, CODE_TTL);

        // 2) 메일 전송 (단순 텍스트)
        String subject = "[Fitu] 이메일 인증코드";
        String text = String.format(
                "인증코드: %s\n",
                code, CODE_TTL.toMinutes()
        );
        sendMail(email, subject, text);
    }

    @Override
    public boolean verifyCode(String email, String code) {
        String key = "email:verify:" + email;
        String saved = redisTemplate.opsForValue().get(key);
        if (saved != null && saved.equalsIgnoreCase(code)) {
            // 일치 → 1회성 사용: 바로 삭제
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }

    private void sendMail(String to, String subject, String text) {
        var msg = new org.springframework.mail.SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(text);
        mailSender.send(msg);
    }

    // 대문자+숫자 6자리
    private String generateCode(int len) {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        java.security.SecureRandom r = new java.security.SecureRandom();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(chars.charAt(r.nextInt(chars.length())));
        return sb.toString();
    }
}
