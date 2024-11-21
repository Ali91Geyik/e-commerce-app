package org.allisra.ecommerceapp.exception;

import jakarta.security.auth.message.callback.PrivateKeyCallback;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.allisra.ecommerceapp.model.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j  // lombok loglama
@RestControllerAdvice // tüm controllerlar için hata kontrol denetimi
public class GlobalExceptionHandler {

    //Kaynak bulunamadığında
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .requestId(UUID.randomUUID().toString())
                .build();
        log.error("Resource not found exception: {}" , error);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    //Validation hatalarında
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ){

        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error)->
        {
        String fieldName = ((FieldError)error).getField();
        String errorMessage = error.getDefaultMessage();
        validationErrors.put(fieldName,errorMessage);
        });

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Validation failed")
                .path(request.getRequestURI())
                .requestId(UUID.randomUUID().toString())
                .validationErrors(validationErrors)
                .build();

        log.error("Validation exception: {}", error);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    //Genel hatalarda
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUncaughtExceptions(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Uncaught error: ", ex); // Stack trace'i görmek için
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message(ex.getMessage()) // Gerçek hata mesajını gösterelim
                .path(request.getRequestURI())
                .requestId(UUID.randomUUID().toString())
                .build();

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
