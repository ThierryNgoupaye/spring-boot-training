package tech.formation.springSecurityTraining.DTO.resquestDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SendCodeRequestDTO(

        @NotBlank(message = "L'email ne peut etre vide")
        @Email(message = "entrer une email valide")
        String email
) {
}
