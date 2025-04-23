package com.hsp.fitu.service;

import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    boolean oAuthLogin(String accessCode, HttpServletResponse httpServletResponse);
}
