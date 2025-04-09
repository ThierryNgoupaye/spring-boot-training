package tech.formation.springSecurityTraining.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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
import tech.formation.springSecurityTraining.DTO.ApiError;
import tech.formation.springSecurityTraining.DTO.ApiResponse;
import tech.formation.springSecurityTraining.DTO.responseDTO.AuthenticationResponseDTO;
import tech.formation.springSecurityTraining.DTO.resquestDTO.AuthentificationDTO;
import tech.formation.springSecurityTraining.DTO.resquestDTO.ResetPasswordRequestDTO;
import tech.formation.springSecurityTraining.DTO.resquestDTO.SendCodeRequestDTO;
import tech.formation.springSecurityTraining.entite.Jwt;
import tech.formation.springSecurityTraining.entite.Utilisateur;
import tech.formation.springSecurityTraining.securite.JwtService;
import tech.formation.springSecurityTraining.service.NotificationService;
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
    private final ValidationService validationService;


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


    @PostMapping(path = "connexion", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<AuthenticationResponseDTO>> connexion(@RequestBody @Valid AuthentificationDTO authentificationDTO)
    {
        final Authentication authentication;
       /* if (authentificationDTO.username() == null || authentificationDTO.password() == null)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mot de Passe et nom obligatoires");
        }*/
        try
        {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authentificationDTO.username(), authentificationDTO.password()));
            if(authentication.isAuthenticated())
            {
                Map<String, Object> mp  = this.jwtService.generate(authentificationDTO.username());
                AuthenticationResponseDTO authenticationResponseDTO = AuthenticationResponseDTO.builder().nom((String) mp.get("nom")).id((String) mp.get("id")).role((String) mp.get("role")).Credentials(mp.get("Credential")).username((String) mp.get("username")).build();
                ApiResponse<AuthenticationResponseDTO> apiResponse = new ApiResponse<>();
                apiResponse.setData(authenticationResponseDTO);
                apiResponse.setStatus(String.valueOf(HttpStatus.OK.value()));
                apiResponse.setDescription("");
                return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
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


    @PostMapping(path = "deconnexion", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
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
                        .data(jwt)
                        .build();
                return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
            }
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Vous n'êtes pas connecté ou token invalide");
    }


    @PostMapping(path = "changer-mot-de-passe", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Object>> changerMotDePasse(@Valid @RequestBody ResetPasswordRequestDTO resetPasswordRequestDTO)
    {
        this.utilisateurService.changerMotDePasse(resetPasswordRequestDTO.email(), resetPasswordRequestDTO.newPassword(), resetPasswordRequestDTO.codeActivation());
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                                                        .data("Mot de passe modifie avec succes")
                                                        .status(String.valueOf(HttpStatus.OK))
                                                        .description("")
                                                        .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }


    @PostMapping(path = "envoyer-code", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Object>> envoyerCode(@RequestBody @Valid SendCodeRequestDTO sendCodeRequestDTO)
    {
        String code = this.utilisateurService.envoyerCode(sendCodeRequestDTO.email());


        ApiResponse<Object> apiResponse = ApiResponse.builder()
                                                     .status(String.valueOf(HttpStatus.OK))
                                                     .data("Code envoye avec succes: " + code)
                                                     .description("")
                                                     .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }


    @GetMapping(path = "refresh-token", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Object>> getAllUtilisateur(HttpServletRequest request)
    {
        String authorization = request.getHeader("Authorization");
        if(authorization != null && authorization.startsWith("Bearer"))
        {
            String token = authorization.substring(7);
            Jwt jwt = this.jwtService.getTokenByValue(token);
            if(jwt != null)
            {
                Map<String, Object> refreshToken = this.jwtService.generate(jwt.getUtilisateur().getUsername());
                ApiResponse<Object> apiResponse = ApiResponse.builder()
                        .status(String.valueOf(HttpStatus.OK))
                        .data(refreshToken.get("Credential"))
                        .description("")
                        .build();
                return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
            }
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Il manque le token dans votre requete");
        }
        return null;
    }
}




