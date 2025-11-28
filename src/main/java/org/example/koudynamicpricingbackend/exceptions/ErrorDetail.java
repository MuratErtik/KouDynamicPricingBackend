package org.example.koudynamicpricingbackend.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetail {

    private String error;

    private String details;

    private LocalDateTime timestamp;
}
