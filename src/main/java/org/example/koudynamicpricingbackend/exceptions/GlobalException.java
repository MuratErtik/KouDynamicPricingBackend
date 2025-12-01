package org.example.koudynamicpricingbackend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalException {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorDetail> handleAuthException(AuthException ae, WebRequest request) {

        ErrorDetail errorDetail = new ErrorDetail();

        errorDetail.setTimestamp(LocalDateTime.now());

        errorDetail.setError(ae.getMessage());

        errorDetail.setDetails(request.getDescription(false));

        return new ResponseEntity<>(errorDetail, HttpStatus.BAD_REQUEST);

    }

    //If @Valid gets to fail then this method gonna execute
    //return to frontend which fields have error
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        // iterate to all fields which got errors and insert map like "email":"invalid format"
        ex.getBindingResult().getAllErrors().forEach((error) -> {

            String fieldName = ((FieldError) error).getField();

            String errorMessage = error.getDefaultMessage();

            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AirportException.class)
    public ResponseEntity<ErrorDetail> handleAirportException(AirportException ae, WebRequest request) {

        ErrorDetail errorDetail = new ErrorDetail();

        errorDetail.setTimestamp(LocalDateTime.now());

        errorDetail.setError(ae.getMessage());

        errorDetail.setDetails(request.getDescription(false));

        return new ResponseEntity<>(errorDetail, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(FlightException.class)
    public ResponseEntity<ErrorDetail> handleFlightException(FlightException ae, WebRequest request) {

        ErrorDetail errorDetail = new ErrorDetail();

        errorDetail.setTimestamp(LocalDateTime.now());

        errorDetail.setError(ae.getMessage());

        errorDetail.setDetails(request.getDescription(false));

        return new ResponseEntity<>(errorDetail, HttpStatus.BAD_REQUEST);

    }

}
