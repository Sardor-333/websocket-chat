package com.example.websocketgroupdemo.payload.req;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterDto {

    @NotBlank
    String fullName;

    @NotBlank
    String username;

    @NotBlank
    String password;

    MultipartFile img;
}
