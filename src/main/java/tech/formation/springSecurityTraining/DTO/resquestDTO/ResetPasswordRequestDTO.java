package tech.formation.springSecurityTraining.DTO.resquestDTO;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


public record ResetPasswordRequestDTO (
        @NotBlank(message = "L'adresse email ne peut pas être vide")
        @Email(message = "Le format de l'adresse email est invalide")
        String email,
        @NotBlank(message = "Le mot de passe ne peut pas être vide")
        String newPassword,
        @NotBlank(message = "Le code d'activation doit etre renseigne.")
        String codeActivation
) {}
