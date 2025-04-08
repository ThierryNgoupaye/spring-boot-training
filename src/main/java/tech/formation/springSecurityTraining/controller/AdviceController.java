package tech.formation.springSecurityTraining.controller;


import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import tech.formation.springSecurityTraining.DTO.ApiError;


import java.util.HashMap;
import java.util.Map;


import static org.springframework.http.HttpStatus.*;

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


    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception e) {
        ApiError apiError = new ApiError();
        apiError.setData("Une erreur interne est survenue lors dans le traitement de la requete");
        apiError.setDescription(e.getLocalizedMessage());
        apiError.setStatus(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        // Créer un message d'erreur consolidé
      /*  String errorMsg = errors.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));
*/
        ApiError apiError = ApiError.builder()
                .status(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                .data(errors)
                .description("Erreur de validation")
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }






}