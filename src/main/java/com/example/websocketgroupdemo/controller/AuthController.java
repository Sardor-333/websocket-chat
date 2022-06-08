package com.example.websocketgroupdemo.controller;

import com.example.websocketgroupdemo.payload.req.RegisterDto;
import com.example.websocketgroupdemo.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@ModelAttribute RegisterDto registerDto) {
        return authService.register(registerDto);
    }
}
