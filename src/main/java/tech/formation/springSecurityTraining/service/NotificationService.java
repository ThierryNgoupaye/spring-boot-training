package tech.formation.springSecurityTraining.service;


import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import tech.formation.springSecurityTraining.entite.Validation;

@Service
@AllArgsConstructor
public class NotificationService {

    private JavaMailSender javaMailSender;



    public void envoyerNotification(@NotNull Validation validation)
    {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("thierryngoupaye0@gmail.com");
        mailMessage.setTo(validation.getUtilisateur().getEmail());
        mailMessage.setSubject("Votre code d'Activation");
        String texte = String.format("Bonjour %s  , Votre code d'activation est %s: ", validation.getUtilisateur().getNom(), validation.getCode());
        mailMessage.setText(texte);
        javaMailSender.send(mailMessage);

    }
}
