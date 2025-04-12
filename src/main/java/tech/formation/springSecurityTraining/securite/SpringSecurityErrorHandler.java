package tech.formation.springSecurityTraining.securite;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import tech.formation.springSecurityTraining.DTO.ApiError;

import java.io.IOException;




public class SpringSecurityErrorHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    @Override
    public void commence(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull AuthenticationException authException) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        ApiError apiError = ApiError.builder()
                .status(String.valueOf(HttpStatus.UNAUTHORIZED.value()))
                .data(authException.getMessage())
                .description("Vous dvez etre connecte pour acceder a cette ressource")
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonError = objectMapper.writeValueAsString(apiError);
        response.getWriter().write(jsonError);
    }


    @Override
    public void handle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull AccessDeniedException accessDeniedException) throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        ApiError apiError = ApiError.builder()
                .status(String.valueOf(HttpStatus.FORBIDDEN.value()))
                .data(accessDeniedException.getMessage())
                .description("Acces refuse : vous n'avez pas les autorisations necessaires pour acceder a cette ressource.")
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonError = objectMapper.writeValueAsString(apiError);
        response.getWriter().write(jsonError);
    }
}
