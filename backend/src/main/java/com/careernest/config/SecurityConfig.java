package com.careernest.config;

import com.careernest.common.dto.ApiResponse;
import com.careernest.common.security.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter,
                                            AuthenticationProvider authenticationProvider,
                                            CorsConfigurationSource corsConfigurationSource,
                                            ObjectMapper objectMapper) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(errors -> errors
                    .authenticationEntryPoint((request, response, exception) -> {
                        response.setStatus(401);
                        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                        objectMapper.writeValue(response.getOutputStream(),
                            ApiResponse.failure("Authentication is required", null));
                    })
                    .accessDeniedHandler((request, response, exception) -> {
                        response.setStatus(403);
                        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                        objectMapper.writeValue(response.getOutputStream(),
                            ApiResponse.failure("Access denied", null));
                    }))
                .authenticationProvider(authenticationProvider)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/signup", "/api/v1/auth/login", "/api/v1/auth/refresh-token",
                            "/api/v1/auth/logout",
                                "/api/v1/auth/forgot-password", "/api/v1/auth/reset-password",
                                "/api/v1/auth/verify-email", "/api/v1/auth/resend-verification",
                                "/api/v1/health", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/public/**").permitAll()
                        .requestMatchers("/api/v1/super-admin/**").hasRole("SUPER_ADMIN")
                        .requestMatchers("/api/v1/admin/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
