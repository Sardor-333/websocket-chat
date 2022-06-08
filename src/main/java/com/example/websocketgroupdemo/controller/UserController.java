package com.example.websocketgroupdemo.controller;

import com.example.websocketgroupdemo.payload.resp.ApiResponse;
import com.example.websocketgroupdemo.projection.UserProjection;
import com.example.websocketgroupdemo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProjection>> getMe(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.getMe(userDetails);
    }

    @GetMapping("/myContacts")
    public ResponseEntity<ApiResponse<List<UserProjection>>> getMyContacts(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.getMyContacts(userDetails);
    }
}
