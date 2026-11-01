package com.careernest.modules.user.service;

import com.careernest.common.exception.ResourceNotFoundException;
import com.careernest.modules.user.entity.Role;
import com.careernest.modules.user.entity.RoleName;
import com.careernest.modules.user.repository.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role required(RoleName roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not configured: " + roleName));
    }
}
