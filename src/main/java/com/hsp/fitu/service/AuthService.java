package com.hsp.fitu.service;

import com.hsp.fitu.dto.LoginDTO;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    LoginDTO oAuthLogin(String accessCode, HttpServletResponse httpServletResponse);
}
