package com.careernest.modules.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.test.web.servlet.MockMvc;
import com.careernest.modules.audit.service.AuditLogService;
import com.careernest.modules.user.entity.AccountStatus;
import com.careernest.modules.user.entity.RoleName;
import com.careernest.modules.user.entity.User;
import com.careernest.modules.user.repository.RoleRepository;
import com.careernest.modules.user.repository.UserRepository;

import jakarta.servlet.http.Cookie;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(AuthControllerSecurityTest.SuperAdminProbeController.class)
class AuthControllerSecurityTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuditLogService auditLogService;

    @Test
    void protectedMeEndpointRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
            .andExpect(status().isUnauthorized());
        }

    @Test
    void protectedEndpointReturnsStandardUnauthorizedEnvelope() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Authentication is required"));
    }

        @Test
        void studentCannotAccessAdminApi() throws Exception {
        mockMvc.perform(get("/api/v1/admin/overview").with(user("student").roles("STUDENT")))
            .andExpect(status().isForbidden());
        }

        @Test
        void adminCanAccessAdminApi() throws Exception {
        mockMvc.perform(get("/api/v1/admin/overview").with(user("admin").roles("ADMIN")))
            .andExpect(status().isOk());
        }

        @Test
        void adminCannotAccessSuperAdminNamespace() throws Exception {
        mockMvc.perform(get("/api/v1/super-admin/probe").with(user("admin").roles("ADMIN")))
            .andExpect(status().isForbidden());
        }

        @Test
        void superAdminPassesSuperAdminAuthorization() throws Exception {
        mockMvc.perform(get("/api/v1/super-admin/probe").with(user("owner").roles("SUPER_ADMIN")))
                    .andExpect(status().isOk());
    }

    @Test
    void healthEndpointIsPublic() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk());
    }

        @Test
        void openApiDocumentIsPublic() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
            .andExpect(status().isOk());
        }

        @Test
        void adminAuditLogSearchMapsLazyUsersAndPagesResults() throws Exception {
            String email = "audit-flow-" + UUID.randomUUID() + "@example.com";
            User studentUser = new User();
            studentUser.setFullName("Audit Flow User");
            studentUser.setEmail(email);
            studentUser.setPasswordHash(passwordEncoder.encode("SecurePass@123"));
            studentUser.setRole(roleRepository.findByName(RoleName.STUDENT).orElseThrow());
            studentUser.setEmailVerified(true);
            studentUser.setAccountStatus(AccountStatus.ACTIVE);
            userRepository.save(studentUser);
            auditLogService.record(studentUser, studentUser, "RUNTIME_AUDIT_PROBE", "H2 audit mapping test", "127.0.0.1", "JUnit");

            mockMvc.perform(get("/api/v1/admin/audit-logs")
                            .queryParam("query", "RUNTIME_AUDIT_PROBE")
                            .queryParam("page", "0")
                            .queryParam("size", "1")
                            .with(user("admin").roles("ADMIN")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content[0].userEmail").value(email))
                    .andExpect(jsonPath("$.data.content[0].actorEmail").value(email))
                    .andExpect(jsonPath("$.data.number").value(0))
                    .andExpect(jsonPath("$.data.size").value(1));
        }

        @Test
        void refreshTokenUsesHttpOnlyCookieAndRotatesOnRefresh() throws Exception {
        String email = "cookie-flow-" + UUID.randomUUID() + "@example.com";
        User user = new User();
        user.setFullName("Cookie Flow User");
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode("SecurePass@123"));
        user.setRole(roleRepository.findByName(RoleName.STUDENT).orElseThrow());
        user.setEmailVerified(true);
        user.setAccountStatus(AccountStatus.ACTIVE);
        userRepository.save(user);

        var login = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"" + email + "\",\"password\":\"SecurePass@123\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
            .andExpect(jsonPath("$.data.refreshToken").value(org.hamcrest.Matchers.nullValue()))
            .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("HttpOnly")))
            .andReturn();
        Cookie loginCookie = login.getResponse().getCookie("careernest.refresh");
        org.assertj.core.api.Assertions.assertThat(loginCookie).isNotNull();

        var refresh = mockMvc.perform(post("/api/v1/auth/refresh-token").cookie(loginCookie))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
            .andExpect(jsonPath("$.data.refreshToken").value(org.hamcrest.Matchers.nullValue()))
            .andExpect(cookie().exists("careernest.refresh"))
            .andReturn();
        Cookie rotatedCookie = refresh.getResponse().getCookie("careernest.refresh");
        org.assertj.core.api.Assertions.assertThat(rotatedCookie.getValue()).isNotEqualTo(loginCookie.getValue());

        mockMvc.perform(post("/api/v1/auth/logout").cookie(rotatedCookie))
            .andExpect(status().isOk())
            .andExpect(cookie().maxAge("careernest.refresh", 0));
        }

    @RestController
    static class SuperAdminProbeController {
        @GetMapping("/api/v1/super-admin/probe")
        String probe() {
            return "ok";
        }
    }
}
