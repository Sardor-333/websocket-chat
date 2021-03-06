package com.example.websocketgroupdemo.payload.resp;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtResponse {

    Long userId;
    String username;
    String accessTokenType;
    String accessToken;
}
