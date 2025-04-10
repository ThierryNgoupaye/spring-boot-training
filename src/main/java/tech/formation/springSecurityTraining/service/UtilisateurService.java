package tech.formation.springSecurityTraining.service;

import jakarta.transaction.Status;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tech.formation.springSecurityTraining.DTO.resquestDTO.ResetPasswordRequestDTO;
import tech.formation.springSecurityTraining.entite.Role;
import tech.formation.springSecurityTraining.entite.Utilisateur;
import tech.formation.springSecurityTraining.entite.Validation;
import tech.formation.springSecurityTraining.enumeration.TypeDeRole;
import tech.formation.springSecurityTraining.repository.UtilisateurRepository;

import java.awt.*;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UtilisateurService implements UserDetailsService {

    private  UtilisateurRepository utilisateurRepository;
    private  BCryptPasswordEncoder passwordEncoder;
    private  ValidationService validationService;


    public void inscription(@NotNull Utilisateur utilisateur)
    {
        if (!utilisateur.getEmail().contains("@") || !utilisateur.getEmail().contains("."))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Votre email est invalide");
        }
        Optional<Utilisateur> user = this.utilisateurRepository.findByEmail(utilisateur.getEmail());

        if (user.isPresent())
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cet utilisateur existe deja dans le systeme");
        }
        String mdpChiffre= this.passwordEncoder.encode(utilisateur.getMdp());
        utilisateur.setMdp(mdpChiffre);
        Role roleUtilisateur = new Role();
        roleUtilisateur.setLibelle(TypeDeRole.UTILISATEUR);
        utilisateur.setRole(roleUtilisateur);
        utilisateur = this.utilisateurRepository.save(utilisateur);
        this.validationService.enregistrer(utilisateur);
    }


    public void activation(@NotNull Map<String, String> activation)
    {
        Validation validation = this.validationService.lireEnFonctionDuCode(activation.get("code"));
        if (Instant.now().isAfter(validation.getExpiration()))
        {
            throw new RuntimeException("Votre code a expire");
        }
        else
        {
           Utilisateur utilisateurActive = this.loadUserById(validation.getUtilisateur().getId());
           utilisateurActive.setActif(true);
           this.utilisateurRepository.save(utilisateurActive);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.utilisateurRepository.findByEmail(username)
                                            .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"aucun utilisateur ne correspond a cet username"));
    }


    public Utilisateur loadUserById(Integer id)
    {
        return this.utilisateurRepository.findById(id)
                                            .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "aucun utilisateur ne correspond a cet identifiant"));
    }


    public String envoyerCode(String email) {
        Utilisateur utilisateur = (Utilisateur) this.loadUserByUsername(email);
        Validation validation = this.validationService.enregistrer(utilisateur);
        return validation.getCode();
    }

    public void changerMotDePasse(String email, String newPassword, String codeActivation) {
        final Validation validation = this.validationService.lireEnFonctionDuCode(codeActivation);
        if(Instant.now().isAfter(validation.getExpiration()))
        {
            throw new ResponseStatusException(HttpStatusCode.valueOf(401),"Votre code a expire");
        }
        if(!validation.getUtilisateur().getEmail().equals(email))
        {
            throw new ResponseStatusException(HttpStatusCode.valueOf(401),"L'email fourni ne correspond a celui a qui appartient ce code de validation");
        }
        Utilisateur utilisateur = validation.getUtilisateur();
        utilisateur.setMdp(this.passwordEncoder.encode(newPassword));
        this.utilisateurRepository.save(utilisateur);
    }

    public Utilisateur getUtilisateurConnecte()
    {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof Utilisateur utilisateur) {
            return utilisateur;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aucun utilisateur authentifié trouve");
        }
    }
}
