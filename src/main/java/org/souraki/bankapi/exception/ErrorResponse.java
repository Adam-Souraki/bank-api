package org.souraki.bankapi.exception;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorResponse {
    private LocalDateTime timestamp;
    private String error;
    private String message;
    private String path;

    public ErrorResponse() {}

    public ErrorResponse(String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.error = error;
        this.message = message;
        this.path = path;
    }
}
