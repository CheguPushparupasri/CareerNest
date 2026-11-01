package com.careernest.modules.auth.controller;

import com.careernest.common.dto.ApiResponse;
import com.careernest.modules.auth.dto.*;
import com.careernest.modules.auth.service.AuthService;
import com.careernest.modules.auth.service.EmailService;
import com.careernest.modules.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication")
public class AuthController {
    private static final String REFRESH_COOKIE = "careernest.refresh";
    private final AuthService authService;
    private final EmailService emailService;
    private final long refreshTokenDays;
    private final boolean secureRefreshCookie;

    public AuthController(AuthService authService, EmailService emailService,
                          @Value("${app.jwt.refresh-token-days}") long refreshTokenDays,
                          @Value("${app.auth.refresh-cookie-secure:false}") boolean secureRefreshCookie) {
        this.authService = authService;
        this.emailService = emailService;
        this.refreshTokenDays = refreshTokenDays;
        this.secureRefreshCookie = secureRefreshCookie;
    }

    @PostMapping("/signup")
    public ApiResponse<UserResponse> signup(@Valid @RequestBody SignupRequest request, HttpServletRequest servletRequest) {
        String message = emailService.isConfigured()
                ? "Signup successful. Please verify your email."
                : "Signup successful. Verification is required, but email delivery is not configured.";
        return ApiResponse.success(message,
                authService.signup(request, ip(servletRequest), servletRequest.getHeader("User-Agent")));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest,
                                           HttpServletResponse servletResponse) {
        AuthResponse result = authService.login(request, ip(servletRequest), servletRequest.getHeader("User-Agent"));
        setRefreshCookie(servletResponse, result.refreshToken());
        return ApiResponse.success("Login successful", withoutRefreshToken(result));
    }

    @PostMapping("/refresh-token")
    public ApiResponse<AuthResponse> refresh(@RequestBody(required = false) TokenRefreshRequest request,
                                             @CookieValue(name = REFRESH_COOKIE, required = false) String cookieToken,
                                             HttpServletResponse servletResponse) {
        String rawToken = StringUtils.hasText(cookieToken) ? cookieToken : request == null ? null : request.refreshToken();
        if (!StringUtils.hasText(rawToken)) {
            throw new com.careernest.common.exception.UnauthorizedException("Refresh token is required");
        }
        AuthResponse result = authService.refresh(new TokenRefreshRequest(rawToken));
        setRefreshCookie(servletResponse, result.refreshToken());
        return ApiResponse.success("Token refreshed", withoutRefreshToken(result));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody(required = false) TokenRefreshRequest request,
                                    @CookieValue(name = REFRESH_COOKIE, required = false) String cookieToken,
                                    HttpServletResponse servletResponse) {
        String rawToken = StringUtils.hasText(cookieToken) ? cookieToken : request == null ? null : request.refreshToken();
        if (StringUtils.hasText(rawToken)) {
            authService.logout(new TokenRefreshRequest(rawToken));
        }
        clearRefreshCookie(servletResponse);
        return ApiResponse.success("Logged out successfully");
    }

    @PostMapping("/logout-all")
    public ApiResponse<Void> logoutAll(HttpServletResponse servletResponse) {
        authService.logoutAll();
        clearRefreshCookie(servletResponse);
        return ApiResponse.success("All sessions revoked");
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> me() {
        return ApiResponse.success("Current user fetched", authService.me());
    }

    @PostMapping("/forgot-password")
    public ApiResponse<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        String message = emailService.isConfigured()
            ? "If the email exists, password reset instructions have been sent."
            : "If the email exists, instructions can be sent after email delivery is configured.";
        return ApiResponse.success(message);
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ApiResponse.success("Password reset successful");
    }

    @PostMapping("/verify-email")
    public ApiResponse<Void> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        authService.verifyEmail(request);
        return ApiResponse.success("Email verified successfully");
    }

    @PostMapping("/resend-verification")
    public ApiResponse<Void> resendVerification(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.resendVerification(request);
        String message = emailService.isConfigured()
            ? "If the account needs verification, instructions have been sent."
            : "If the account needs verification, instructions can be sent after email delivery is configured.";
        return ApiResponse.success(message);
    }

    @GetMapping("/sessions")
    public ApiResponse<List<SessionResponse>> sessions() {
        return ApiResponse.success("Sessions fetched", authService.sessions());
    }

    @DeleteMapping("/sessions/{sessionId}")
    public ApiResponse<Void> revokeSession(@PathVariable Long sessionId) {
        authService.revokeSession(sessionId);
        return ApiResponse.success("Session revoked");
    }

    private String ip(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        return forwarded == null ? request.getRemoteAddr() : forwarded.split(",")[0].trim();
    }

    private AuthResponse withoutRefreshToken(AuthResponse response) {
        return new AuthResponse(response.accessToken(), null, response.tokenType(), response.expiresIn(), response.user());
    }

    private void setRefreshCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_COOKIE, token)
                .httpOnly(true)
                .secure(secureRefreshCookie)
                .sameSite("Lax")
                .path("/api/v1/auth")
                .maxAge(Duration.ofDays(refreshTokenDays))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearRefreshCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_COOKIE, "")
                .httpOnly(true)
                .secure(secureRefreshCookie)
                .sameSite("Lax")
                .path("/api/v1/auth")
                .maxAge(Duration.ZERO)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
