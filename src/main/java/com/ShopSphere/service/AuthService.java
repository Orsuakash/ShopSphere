package com.ShopSphere.service;

import com.ShopSphere.DTOS.AuthResponse;
import com.ShopSphere.DTOS.LoginRequest;
import com.ShopSphere.DTOS.RegisterRequest;
import com.ShopSphere.Entity.Role;
import com.ShopSphere.Entity.User;
import com.ShopSphere.ExceptionHandling.BadRequestException;
import com.ShopSphere.Repository.RoleRepository;
import com.ShopSphere.Repository.UserRepository;
import com.ShopSphere.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureProxyUntrustedEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Set;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    //Registation

    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new BadRequestException("Username is already in use");


        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Email is alredy exists");
        }

        Role userrole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new BadRequestException("USER role not found"));


        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))

                .enabled(true)
                .roles(Set.of(userrole))
                .build();

        User savedUser = userRepository.save(user);

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername(savedUser.getUsername())
                        .password(savedUser.getPassword())
                        .authorities("ROLE_USER")
                        .build();


        String token = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .username(savedUser.getUsername())
                .message("Registation Seussfully")
                .build();


    }

    // Login
    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword())
        );

        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();

        String token = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .username(userDetails.getUsername())
                .message("Login Seussfully")
                .build();

    }
}
