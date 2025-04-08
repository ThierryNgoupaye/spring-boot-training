package tech.formation.springSecurityTraining.service;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
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
            throw new RuntimeException("Votre email est invalide");
        }
        Optional<Utilisateur> user = this.utilisateurRepository.findByEmail(utilisateur.getEmail());

        if (user.isPresent())
        {
            throw new RuntimeException("utilisateur deja present");
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
           Utilisateur utilisateurActive = this.utilisateurRepository.findById(validation.getUtilisateur().getId()).orElseThrow(()->new RuntimeException("Utilisateur inconnu"));
           utilisateurActive.setActif(true);
           this.utilisateurRepository.save(utilisateurActive);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.utilisateurRepository.findByEmail(username).orElseThrow(()->new RuntimeException("aucun utilisateur ne correspond a cet identifiant"));
    }


    public String envoyerCode(String email) {
        Utilisateur utilisateur = (Utilisateur) this.loadUserByUsername(email);
        Validation validation = this.validationService.enregistrer(utilisateur);
        return validation.getCode();
    }

    public void changerMotDePasse(String email, String newPassword, String codeActivation) {
        Validation validation = this.validationService.lireEnFonctionDuCode(codeActivation);
        if(Instant.now().isAfter(validation.getExpiration()))
        {
            throw new RuntimeException("Votre code a expire");
        }
        if(!validation.getUtilisateur().getEmail().equals(email))
        {
            throw new RuntimeException("L'email fourni ne correspond a celui a qui appartient ce code de validation");
        }
        Utilisateur utilisateur = validation.getUtilisateur();
        utilisateur.setMdp(this.passwordEncoder.encode(newPassword));
        this.utilisateurRepository.save(utilisateur);
    }
}
