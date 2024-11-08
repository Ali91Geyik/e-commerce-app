package org.allisra.ecommerceapp.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status; // Http durum kodu
    private String message;  // Hata mesajı
    private String path; //Hatanın oluştuğu endpoint
    private String error; // Hata tipi
    private String requestId; // istek takibi için unique id
    private Map<String, String> validationErrors;


}
