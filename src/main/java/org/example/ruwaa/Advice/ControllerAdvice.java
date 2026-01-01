package org.example.ruwaa.Advice;


import jakarta.servlet.ServletException;
import jakarta.validation.ConstraintDeclarationException;
import jakarta.validation.ConstraintViolationException;
import org.example.ruwaa.Api.ApiException;
import org.example.ruwaa.Api.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice
{
    @ExceptionHandler(value = ApiException.class)
    public ResponseEntity<?> apiException(ApiException e){
        return ResponseEntity.status(400).body(new ApiResponse(e.getMessage()));
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<?> methodArgumentNotValidException(MethodArgumentNotValidException ex){
        return ResponseEntity.status(400).body(new ApiResponse(ex.getFieldError().getDefaultMessage()));
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<?> ConstraintViolationException(ConstraintDeclarationException ex){
        return ResponseEntity.status(400).body(new ApiResponse(ex.getMessage()));
    }


    @ExceptionHandler(value = SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<?> SQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException ex){
        return ResponseEntity.status(400).body(new ApiResponse(ex.getLocalizedMessage()));
    }

    @ExceptionHandler(value = InvalidDataAccessResourceUsageException.class)
    public ResponseEntity<?> InvalidDataAccessResourceUsageException(InvalidDataAccessResourceUsageException ex){
        return ResponseEntity.status(400).body(new ApiResponse(ex.getMessage()));
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public ResponseEntity<?> DataIntegrityViolationException(DataIntegrityViolationException ex){
        return ResponseEntity.status(400).body(new ApiResponse(ex.getMessage()));
    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> HttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex){
        return ResponseEntity.status(400).body(new ApiResponse(ex.getMessage()));
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<?> HttpMessageNotReadableException(HttpMessageNotReadableException ex){
        return ResponseEntity.status(400).body(new ApiResponse(ex.getMessage()));
    }

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> MethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex){
        return ResponseEntity.status(400).body(new ApiResponse(ex.getMessage()));
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<?> Exception(Exception e){
        return ResponseEntity.status(400).body(new ApiResponse(e.getMessage()));
    }

    @ExceptionHandler(value = UsernameNotFoundException.class)
    public ResponseEntity<?> UsernameNotFoundException(UsernameNotFoundException e){
        return ResponseEntity.status(400).body(new ApiResponse(e.getMessage()));
    }

    @ExceptionHandler(value = ServletException.class)
    public ResponseEntity<?> ServletException(ServletException e){
        return ResponseEntity.status(400).body(new ApiResponse(e.getMessage()));
    }

    @ExceptionHandler(value = IOException.class)
    public ResponseEntity<?> IOException(IOException e){
        return ResponseEntity.status(400).body(new ApiResponse(e.getMessage()));
    }

}
