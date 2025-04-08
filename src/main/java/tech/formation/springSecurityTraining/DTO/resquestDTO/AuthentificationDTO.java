package tech.formation.springSecurityTraining.DTO.resquestDTO;


import jakarta.validation.constraints.NotBlank;

public record AuthentificationDTO(
        @NotBlank(message = "Le username est obligatoire")
        String username,

        @NotBlank(message = "Le mot de passe est obligatoire")
        String password
) {
}
