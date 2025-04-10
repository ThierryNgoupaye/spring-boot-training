package tech.formation.springSecurityTraining.DTO.responseDTO.Authentication;


import lombok.*;
import tech.formation.springSecurityTraining.DTO.responseDTO.UtilisateurDTO;
import tech.formation.springSecurityTraining.entite.Jwt;
import tech.formation.springSecurityTraining.entite.Utilisateur;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AuthenticationResponseDTO
{

     private UtilisateurDTO utilisateur;
     private JwtResponseDTO credentials;


     public static AuthenticationResponseDTO fromEntityToDTO(Jwt jwt)
     {
          return AuthenticationResponseDTO.builder()
                  .utilisateur(UtilisateurDTO.fromEntityToDTO(jwt.getUtilisateur()))
                  .credentials(
                          JwtResponseDTO.builder()
                                  .bearer(jwt.getValue())
                                  .expirationTime(jwt.getExpirationTime())
                                  .build()
                  )
                  .build();
     }

}
