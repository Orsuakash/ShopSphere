package com.ShopSphere.Controller;

import com.ShopSphere.DTOS.AuthResponse;
import com.ShopSphere.DTOS.LoginRequest;
import com.ShopSphere.DTOS.RegisterRequest;
import com.ShopSphere.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> rigester
            (@RequestBody @Valid RegisterRequest registerRequest){

        AuthResponse registed=authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(registed);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login
            (@RequestBody @Valid LoginRequest loginRequest){
        AuthResponse loginrespo=authService.login(loginRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(loginrespo);
    }
}
