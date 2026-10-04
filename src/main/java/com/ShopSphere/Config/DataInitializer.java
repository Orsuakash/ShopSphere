package com.ShopSphere.Config;

import com.ShopSphere.Entity.Role;
import com.ShopSphere.Entity.User;
import com.ShopSphere.Repository.RoleRepository;
import com.ShopSphere.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        createRoleIfNotExists("USER");
        Role adminRole = createRoleIfNotExists("ADMIN");

        if (userRepository.findByEmail("admin@shopsphere.com").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@shopsphere.com");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setRoles(new HashSet<>(Set.of(adminRole)));
            userRepository.save(admin);
        }
    }

    private Role createRoleIfNotExists(String roleName) {
        return roleRepository.findByRoleName(roleName)
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .roleName(roleName)
                                .build()
                ));
    }
}