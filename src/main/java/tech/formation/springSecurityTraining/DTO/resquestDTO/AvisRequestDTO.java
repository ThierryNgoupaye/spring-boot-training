package tech.formation.springSecurityTraining.DTO.resquestDTO;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tech.formation.springSecurityTraining.entite.Avis;
import tech.formation.springSecurityTraining.entite.Utilisateur;

public record AvisRequestDTO(
        @NotBlank
        String message,

        @NotNull
        Integer idUtilisateur,

        String status
) {
    public static Avis fromDTOtoEntity(AvisRequestDTO avisRequestDTO, Utilisateur utilisateur) {
        return Avis.builder()
                .statut(avisRequestDTO.status() != null ? avisRequestDTO.status() : "")
                .message(avisRequestDTO.message())
                .utilisateur(utilisateur)
                .build();
    }
}
