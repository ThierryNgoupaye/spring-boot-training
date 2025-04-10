package tech.formation.springSecurityTraining.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tech.formation.springSecurityTraining.DTO.ApiResponse;
import tech.formation.springSecurityTraining.DTO.responseDTO.Authentication.AuthenticationResponseDTO;
import tech.formation.springSecurityTraining.DTO.responseDTO.Authentication.JwtResponseDTO;
import tech.formation.springSecurityTraining.DTO.responseDTO.UtilisateurDTO;
import tech.formation.springSecurityTraining.DTO.resquestDTO.AuthentificationDTO;
import tech.formation.springSecurityTraining.DTO.resquestDTO.ResetPasswordRequestDTO;
import tech.formation.springSecurityTraining.DTO.resquestDTO.SendCodeRequestDTO;
import tech.formation.springSecurityTraining.entite.Jwt;
import tech.formation.springSecurityTraining.entite.Role;
import tech.formation.springSecurityTraining.entite.Utilisateur;
import tech.formation.springSecurityTraining.securite.JwtService;
import tech.formation.springSecurityTraining.service.UtilisateurService;
import tech.formation.springSecurityTraining.service.ValidationService;


import java.util.Map;


@Slf4j
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class UtilisateurController {

    private final UtilisateurService utilisateurService;
    private final  AuthenticationManager authenticationManager;
    private final JwtService jwtService;



    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "inscription", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void inscription(@RequestBody Utilisateur utilisateur)
    {
        log.info("inscription");
        this.utilisateurService.inscription(utilisateur);
    }


    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path = "activation", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void activation(@RequestBody Map<String, String> activation)
    {
        log.info("activation du code");
        this.utilisateurService.activation(activation);
    }


    @PostMapping(path = "connexion", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<AuthenticationResponseDTO>> connexion(@RequestBody @Valid AuthentificationDTO authentificationDTO)
    {
        final Authentication authentication;
        try
        {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authentificationDTO.username(), authentificationDTO.password()));
            if(authentication.isAuthenticated())
            {
                Jwt jwt  = this.jwtService.generate(authentificationDTO.username());
                AuthenticationResponseDTO authenticationResponseDTO = AuthenticationResponseDTO.fromEntityToDTO(jwt);
                ApiResponse<AuthenticationResponseDTO> apiResponse = ApiResponse.<AuthenticationResponseDTO>builder()
                                                                                .status(String.valueOf(HttpStatus.OK.value()))
                                                                                .description("Connexion reussie avec succes")
                                                                                .data(authenticationResponseDTO)
                                                                                .build();
                return ResponseEntity.ok(apiResponse);
            }
            else
            {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous n'etes pas authorises");
            }
        }
        catch (BadCredentialsException e)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
        catch (InternalAuthenticationServiceException e)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }



    @PostMapping(path = "deconnexion", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Object>> deconnexion(HttpServletRequest request)
    {

        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer")) {
            String token = authorization.substring(7);
            Jwt jwt = this.jwtService.deconnexion(token);
            if(jwt != null)
            {
                ApiResponse<Object> apiResponse = ApiResponse.builder()
                                                                .status(String.valueOf(HttpStatus.OK))
                                                                .data("Deconnexion reussie avec succes")
                                                                .build();
                return ResponseEntity.ok(apiResponse);
            }
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Vous n'êtes pas connecté ou token invalide");
    }





    @PostMapping(path = "changer-mot-de-passe", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Object>> changerMotDePasse(@Valid @RequestBody ResetPasswordRequestDTO resetPasswordRequestDTO)
    {
        this.utilisateurService.changerMotDePasse(resetPasswordRequestDTO.email(), resetPasswordRequestDTO.newPassword(), resetPasswordRequestDTO.codeActivation());
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                                                        .data("")
                                                        .status(String.valueOf(HttpStatus.OK))
                                                        .description("Mot de passe modifie avec succes")
                                                        .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }




    @PostMapping(path = "envoyer-code", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<String>> envoyerCode(@RequestBody @Valid SendCodeRequestDTO sendCodeRequestDTO)
    {
        String code = this.utilisateurService.envoyerCode(sendCodeRequestDTO.email());


        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                                                     .status(String.valueOf(HttpStatus.OK))
                                                     .data(code)
                                                     .description("Code envoye avec succes")
                                                     .build();
        return ResponseEntity.ok(apiResponse);
    }



    @GetMapping(path = "refresh-token")
    public ResponseEntity<ApiResponse<JwtResponseDTO>> refreshToken(HttpServletRequest request)
    {
        String authorization = request.getHeader("Authorization");
        if(authorization != null && authorization.startsWith("Bearer"))
        {
            String token = authorization.substring(7);
            Jwt jwt = this.jwtService.getTokenByValue(token);
            if(jwt != null)
            {
                Jwt refreshToken = this.jwtService.generate(jwt.getUtilisateur().getUsername());
                JwtResponseDTO jwtResponseDTO = JwtResponseDTO.builder()
                                                                .expirationTime(refreshToken.getExpirationTime())
                                                                .bearer(refreshToken.getValue())
                                                                .build();
                ApiResponse<JwtResponseDTO> apiResponse = ApiResponse.<JwtResponseDTO>builder()
                                                                    .status(String.valueOf(HttpStatus.OK))
                                                                    .data(jwtResponseDTO)
                                                                    .description("Token refraichi avec succes.")
                                                                    .build();
                return ResponseEntity.ok(apiResponse);
            }
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Il manque le token dans votre requete");
        }
        return null;
    }


    @GetMapping(path = "connected-now")
    public ResponseEntity<ApiResponse<UtilisateurDTO>> getCurrentUser()
    {
        Utilisateur utilisateurConnecte = this.utilisateurService.getUtilisateurConnecte();
        ApiResponse<UtilisateurDTO> apiResponse = ApiResponse.<UtilisateurDTO>builder()
                .status(String.valueOf(HttpStatus.OK.value()))
                .description("Utilisateur connecte renvoye avec succes")
                .data(UtilisateurDTO.fromEntityToDTO(utilisateurConnecte))
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}




