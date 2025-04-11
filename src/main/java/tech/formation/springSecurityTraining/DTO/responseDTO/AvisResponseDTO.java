package tech.formation.springSecurityTraining.DTO.responseDTO;


import lombok.*;
import tech.formation.springSecurityTraining.entite.Avis;
import tech.formation.springSecurityTraining.entite.Utilisateur;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AvisResponseDTO {

    private int id;
    private String message;
    private String statut;
    private UtilisateurDTO utilisateur;


    public static AvisResponseDTO fromEntityToDTO(Avis avis) {
        Utilisateur utilisateur = avis.getUtilisateur();
        return AvisResponseDTO.builder()
                .id(avis.getId())
                .message(avis.getMessage())
                .statut(avis.getStatut())
                .utilisateur(UtilisateurDTO.fromEntityToDTO(utilisateur))
                .build();
    }





}
