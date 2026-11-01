package com.careernest.config;

import com.careernest.modules.profile.entity.UserProfile;
import com.careernest.modules.profile.repository.UserProfileRepository;
import com.careernest.modules.user.entity.AccountStatus;
import com.careernest.modules.user.entity.Role;
import com.careernest.modules.user.entity.RoleName;
import com.careernest.modules.user.entity.User;
import com.careernest.modules.user.repository.RoleRepository;
import com.careernest.modules.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Component
public class DataInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final boolean seedRoles;
    private final boolean seedAdmin;
    private final String adminEmail;
    private final String adminPassword;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository, 
                          UserProfileRepository userProfileRepository,
                          PasswordEncoder passwordEncoder,
                          @Value("${app.initialization.seed-roles:false}") boolean seedRoles,
                          @Value("${app.initialization.seed-admin:false}") boolean seedAdmin,
                          @Value("${app.admin.email:}") String adminEmail,
                          @Value("${app.admin.password:}") String adminPassword) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.seedRoles = seedRoles;
        this.seedAdmin = seedAdmin;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (seedRoles) {
            seed(RoleName.STUDENT, "Student or fresher user");
            seed(RoleName.ADMIN, "Platform administrator");
            seed(RoleName.SUPER_ADMIN, "System owner");
        }
        if (seedAdmin) {
            createAdminAccount();
        }
    }

    private void seed(RoleName roleName, String description) {
        roleRepository.findByName(roleName).orElseGet(() -> {
            Role role = new Role();
            role.setName(roleName);
            role.setDescription(description);
            return roleRepository.save(role);
        });
    }

    private void createAdminAccount() {
        if (adminEmail.isBlank() || adminPassword.isBlank()) {
            throw new IllegalStateException("ADMIN_EMAIL and ADMIN_PASSWORD are required when SEED_ADMIN is enabled");
        }
        if (userRepository.existsByEmailIgnoreCase(adminEmail)) {
            return;
        }

        Role adminRole = roleRepository.findByName(RoleName.SUPER_ADMIN)
                .orElseThrow(() -> new IllegalStateException("SUPER_ADMIN role not found"));

        User admin = new User();
        admin.setFullName("System Administrator");
        admin.setEmail(adminEmail.trim().toLowerCase(Locale.ROOT));
        admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        admin.setRole(adminRole);
        admin.setAccountStatus(AccountStatus.ACTIVE);
        admin.setEmailVerified(true);
        admin.setProfileSetupCompleted(true);

        User savedAdmin = userRepository.save(admin);

        UserProfile profile = new UserProfile();
        profile.setUser(savedAdmin);
        profile.setHeadline("System Administrator");
        profile.setLocation("Remote");
        profile.setEducationSummary("System Administrator Account");
        userProfileRepository.save(profile);
    }
}
