package com.example.websocketgroupdemo.payload.req;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginDto {

    @NotBlank
    String username;

    @NotBlank
    String password;
}
