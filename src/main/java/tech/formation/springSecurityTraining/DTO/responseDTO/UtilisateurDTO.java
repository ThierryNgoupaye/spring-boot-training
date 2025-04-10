package tech.formation.springSecurityTraining.DTO.responseDTO;


import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import tech.formation.springSecurityTraining.entite.Role;
import tech.formation.springSecurityTraining.entite.Utilisateur;

import java.util.Collection;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class UtilisateurDTO {


    private int id;
    private boolean actif;
    private String nom;
    private String email;
    private String username;
    private Role role;
    private Collection<? extends GrantedAuthority> authorities;

    public static UtilisateurDTO fromEntityToDTO(Utilisateur utilisateur) {
        return UtilisateurDTO.builder()
                        .id(utilisateur.getId())
                        .nom(utilisateur.getNom())
                        .email(utilisateur.getEmail())
                        .authorities(utilisateur.getAuthorities())
                        .role(utilisateur.getRole())
                        .actif(utilisateur.isActif())
                        .username(utilisateur.getUsername())
                .build();
    }

}
