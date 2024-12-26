package br.com.nszandrew.roadmap.infra.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ExeceptionHandler {


    // 400 - Excessao customizada
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            CustomException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // 403 - Regra de negócio violada
    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            AuthorizationException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.FORBIDDEN.value(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    // 400 - Validação de entrada
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(field -> field.getField() + ": " + field.getDefaultMessage())
                .findFirst()
                .orElse("Erro de validação.");
        ErrorResponse error = new ErrorResponse(message, HttpStatus.BAD_REQUEST.value(), request.getDescription(false));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // 405 - Método HTTP não permitido
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
                "Método " + ex.getMethod() + " não suportado para esta requisição.",
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(error, HttpStatus.METHOD_NOT_ALLOWED);
    }

    // 500 - Exceção genérica
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
                "Erro interno do servidor.",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
