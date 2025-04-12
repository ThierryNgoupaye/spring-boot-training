package tech.formation.springSecurityTraining.controller;


import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import tech.formation.springSecurityTraining.DTO.ApiError;


import java.util.HashMap;
import java.util.Map;


import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class AdviceController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFoundException(EntityNotFoundException e) {
        ApiError apiError = new ApiError();
        apiError.setData(e.getMessage());
        apiError.setDescription(e.getMessage());
        apiError.setStatus(String.valueOf(HttpStatus.BAD_REQUEST.value()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatusException(ResponseStatusException e) {
        ApiError apiError = new ApiError();
        apiError.setData(e.getReason());
        apiError.setDescription(e.getReason());
        apiError.setStatus(String.valueOf(e.getStatusCode().value()));

        return ResponseEntity.status(e.getStatusCode()).body(apiError);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception e) {
        ApiError apiError = new ApiError();
        apiError.setData("Une erreur interne est survenue lors dans le traitement de la requete");
        apiError.setDescription(e.getLocalizedMessage());
        apiError.setStatus(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleRequestMethodNotSupportedException(Exception e) {
        ApiError apiError = new ApiError();
        apiError.setData("la methode que vous avez utilisee n'est pas valide");
        apiError.setDescription(e.getLocalizedMessage());
        apiError.setStatus(String.valueOf(METHOD_NOT_ALLOWED.value()));

        return ResponseEntity.status(METHOD_NOT_ALLOWED).body(apiError);
    }






    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        ApiError apiError = new ApiError();
        apiError.setData(e.getClass().getSimpleName());
        apiError.setDescription("Le corps de la requête est invalide ou manquant.");
        apiError.setStatus(String.valueOf(HttpStatus.BAD_REQUEST.value()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ResponseStatus(FORBIDDEN)
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiError> handleExpiredToken(HttpMessageNotReadableException e) {
        ApiError apiError = new ApiError();
        apiError.setData(e.getClass().getSimpleName());
        apiError.setDescription("Le token est expire.");
        apiError.setStatus(String.valueOf(FORBIDDEN.value()));

        return ResponseEntity.status(FORBIDDEN).body(apiError);
    }



    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ApiError> handleAccountLockedException(LockedException e) {
        return getApiErrorResponseEntity(String.valueOf(e), e.getMessage());
    }


    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiError> handleAccountDisabledException(DisabledException e) {
        return getApiErrorResponseEntity(String.valueOf(e), e.getMessage());
    }

    @ExceptionHandler(AccountExpiredException.class)
    public ResponseEntity<ApiError> handleAccountExpiredException(AccountExpiredException e) {
        return getApiErrorResponseEntity(String.valueOf(e), e.getMessage());
    }

    @NotNull
    private ResponseEntity<ApiError> getApiErrorResponseEntity(String s, String message) {
        log.error(s);
        ApiError apiError = new ApiError();
        apiError.setData("Vous n'etes pas authorise a poursuivre cette requete!");
        apiError.setDescription(message);
        apiError.setStatus(String.valueOf(UNAUTHORIZED.value()));

        return ResponseEntity.status(UNAUTHORIZED).body(apiError);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        ApiError apiError = ApiError.builder()
                .status(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                .data(errors)
                .description("Erreur de validation")
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }






}