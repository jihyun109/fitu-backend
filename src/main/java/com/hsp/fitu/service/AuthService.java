package com.hsp.fitu.service;

import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    void oAuthLogin(String accessCode, HttpServletResponse httpServletResponse);
}
