package com.example.websocketgroupdemo.payload.resp;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    boolean success;
    String errorMessage;
    T data;

    public ApiResponse(String errorMessage) {
        this.errorMessage = errorMessage;
        this.success = false;
    }

    public ApiResponse(T data) {
        this.data = data;
        this.success = true;
    }

    /**
     * for case OK
     */
    public static <T> ResponseEntity<ApiResponse<T>> response(T data) {
        return ResponseEntity.ok(new ApiResponse<>(data));
    }

    /**
     * for ERROR case
     */
    public static <T> ResponseEntity<ApiResponse<T>> response(HttpStatus httpStatus, String errorMessage) {
        return new ResponseEntity<>(new ApiResponse<>(errorMessage), httpStatus);
    }
}
