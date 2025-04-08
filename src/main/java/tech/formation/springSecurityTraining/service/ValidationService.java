package tech.formation.springSecurityTraining.service;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tech.formation.springSecurityTraining.entite.Utilisateur;
import tech.formation.springSecurityTraining.entite.Validation;
import tech.formation.springSecurityTraining.repository.ValidationRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

@Service
@AllArgsConstructor
public class ValidationService {

    private final ValidationRepository validationRepository;
    private final NotificationService notificationService;


    public Validation enregistrer(Utilisateur utilisateur) throws RuntimeException
    {
        Validation validation = new Validation();
        validation.setUtilisateur(utilisateur);
        Instant creation = Instant.now();
        Instant expiration = creation.plus(10, ChronoUnit.MINUTES);

        Random random = new Random();
        int randomInteger = random.nextInt(999999);
        String code = String.format("%06d", randomInteger);
        validation.setCode(code);
        validation.setExpiration(expiration);
        validation.setCreation(creation);

        validation = this.validationRepository.save(validation);
        this.notificationService.envoyerNotification(validation);
        return validation;
    }


    public Validation lireEnFonctionDuCode(String code)
    {
        return this.validationRepository.findByCode(code).orElseThrow(()-> new RuntimeException("Votre code est invalide"));
    }
}
