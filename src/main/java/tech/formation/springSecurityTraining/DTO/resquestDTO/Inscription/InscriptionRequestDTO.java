package tech.formation.springSecurityTraining.DTO.resquestDTO.Inscription;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import tech.formation.springSecurityTraining.entite.Utilisateur;



public record InscriptionRequestDTO(
        @NotBlank
        @Email
        String email,
        @NotBlank
        String nom,
        @NotBlank
        String mdp,

        @NotNull
        RoleDTO role

) {

    public static Utilisateur fromDTOtoEntity(InscriptionRequestDTO inscriptionRequestDTO)
    {
        return Utilisateur.builder()
                .email(inscriptionRequestDTO.email())
                .mdp(inscriptionRequestDTO.mdp())
                .nom(inscriptionRequestDTO.nom())
                .role(RoleDTO.fromDTOtoEntity(inscriptionRequestDTO.role()))
                .build();
    }
}
