package tech.formation.springSecurityTraining.DTO.resquestDTO.Inscription;

import jakarta.validation.constraints.NotNull;
import tech.formation.springSecurityTraining.entite.Role;
import tech.formation.springSecurityTraining.enumeration.TypeDeRole;

public record RoleDTO(
       @NotNull
       TypeDeRole libelle
) {
    public static Role fromDTOtoEntity(RoleDTO roleDTO)
    {
        return Role.builder()
                .libelle(roleDTO.libelle())
                .build();

    }
}
