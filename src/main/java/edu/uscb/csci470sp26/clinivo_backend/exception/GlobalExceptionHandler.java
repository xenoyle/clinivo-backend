package edu.uscb.csci470sp26.clinivo_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handle authentication and authorization errors (invalid credentials)
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        // Check if it's an authentication error
        if (ex.getMessage() != null && 
            (ex.getMessage().contains("Invalid email or password") ||
             ex.getMessage().contains("Invalid credentials"))) {
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", ex.getMessage());
            errorResponse.put("timestamp", String.valueOf(System.currentTimeMillis()));
            
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(errorResponse);
        }
        
        // Handle other runtime exceptions as 400 Bad Request or 500 Internal Server Error
        if (ex.getMessage() != null && 
            (ex.getMessage().contains("User not found") ||
             ex.getMessage().contains("not found"))) {
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", ex.getMessage());
            errorResponse.put("timestamp", String.valueOf(System.currentTimeMillis()));
            
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(errorResponse);
        }
        
        // Default: 500 Internal Server Error
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "An internal server error occurred");
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("timestamp", String.valueOf(System.currentTimeMillis()));
        
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }
}
