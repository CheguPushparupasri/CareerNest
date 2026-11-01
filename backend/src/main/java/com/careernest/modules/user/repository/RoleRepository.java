package com.careernest.modules.user.repository;

import com.careernest.modules.user.entity.Role;
import com.careernest.modules.user.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
