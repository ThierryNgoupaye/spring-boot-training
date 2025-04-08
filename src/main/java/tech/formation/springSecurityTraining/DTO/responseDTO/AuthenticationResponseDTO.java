package tech.formation.springSecurityTraining.DTO.responseDTO;


import lombok.*;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AuthenticationResponseDTO
{

     private String username;
     private String id;
     private String role;
     private String nom;
     private Object Credentials;

}
