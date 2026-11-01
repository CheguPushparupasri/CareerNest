package com.careernest.modules.auth.service;

import com.careernest.modules.auth.dto.*;
import com.careernest.modules.user.dto.UserResponse;

import java.util.List;

public interface AuthService {
    UserResponse signup(SignupRequest request, String ipAddress, String userAgent);
    AuthResponse login(LoginRequest request, String ipAddress, String userAgent);
    AuthResponse refresh(TokenRefreshRequest request);
    void logout(TokenRefreshRequest request);
    void logoutAll();
    UserResponse me();
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
    void verifyEmail(VerifyEmailRequest request);
    void resendVerification(ForgotPasswordRequest request);
    List<SessionResponse> sessions();
    void revokeSession(Long sessionId);
}
