package com.example.websocketgroupdemo.service;

import com.example.websocketgroupdemo.entity.Attachment;
import com.example.websocketgroupdemo.entity.User;
import com.example.websocketgroupdemo.payload.req.RegisterDto;
import com.example.websocketgroupdemo.payload.resp.ApiResponse;
import com.example.websocketgroupdemo.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static com.example.websocketgroupdemo.payload.resp.ApiResponse.response;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<ApiResponse<String>> register(RegisterDto registerDto) {
        if (userRepo.existsByUsername(registerDto.getUsername())) {
            return response(HttpStatus.CONFLICT, "Username: " + registerDto.getUsername() + " already exists!");
        }
        User user = userRepo.save(new User(
                registerDto.getUsername(),
                passwordEncoder.encode(registerDto.getPassword()),
                registerDto.getFullName(),
                false
        ));
        if (registerDto.getImg() != null) {
            try {
                user.setAttachment(Attachment.prepareAttachment(registerDto.getImg()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return response("Successfully registered!");
    }
}
