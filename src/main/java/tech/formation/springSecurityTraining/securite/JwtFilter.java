package tech.formation.springSecurityTraining.securite;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import tech.formation.springSecurityTraining.DTO.ApiError;
import tech.formation.springSecurityTraining.entite.Jwt;
import tech.formation.springSecurityTraining.service.UtilisateurService;

import java.io.IOException;


@Slf4j
@Service
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final UtilisateurService utilisateurService;
    private final JwtService jwtService;


    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String token;
            String username;
            boolean isTokenExpired;
            boolean isTokenDesactive;
            Jwt jwtDansBDD;
            final String authorization = request.getHeader("Authorization");
            if (authorization != null && authorization.startsWith("Bearer"))
            {
                token = authorization.substring(7);
                jwtDansBDD = this.jwtService.getTokenByValue(token);
                isTokenExpired = jwtService.isTokenExpired(token);
                isTokenDesactive= jwtService.isTokenDesactive(token);
                username = this.jwtService.lireUsername(token);

                if (!isTokenExpired && username.equals(jwtDansBDD.getUtilisateur().getUsername()) && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = this.utilisateurService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
                else if(isTokenDesactive)
                {
                    throw new RuntimeException("Le token actuellement fourni a ete desactive");
                }
                else if(isTokenExpired)
                {
                    this.jwtService.desactiverToken(token);
                    throw new RuntimeException("Le token actuellement est expire et donc ete desactive");
                }
            }
            filterChain.doFilter(request, response);
        }
        catch (RuntimeException e)
        {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);

            ApiError apiError = ApiError.builder()
                                        .status(String.valueOf(HttpStatus.FORBIDDEN.value()))
                                        .data("Erreur lors de l'authorisation avec jwt")
                                        .description(e.getMessage())
                                        .build();
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonError = objectMapper.writeValueAsString(apiError);
            response.getWriter().write(jsonError);
        }
    }

}