package tech.formation.springSecurityTraining.service;


import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tech.formation.springSecurityTraining.DTO.resquestDTO.AvisRequestDTO;
import tech.formation.springSecurityTraining.entite.Avis;
import tech.formation.springSecurityTraining.entite.Utilisateur;
import tech.formation.springSecurityTraining.repository.AvisRepository;
import tech.formation.springSecurityTraining.repository.UtilisateurRepository;

import java.util.List;

@AllArgsConstructor
@Service
public class AvisService {

    private final AvisRepository avisRepository;
    private final UtilisateurService utilisateurService;

    public Avis creer(@Valid @NotNull AvisRequestDTO avisDTO)
    {
        Utilisateur utilisateurRequete = this.utilisateurService.loadUserById(avisDTO.idUtilisateur());
        Utilisateur utilisateur = this.utilisateurService.getUtilisateurConnecte();
        if (utilisateur.getId().equals(utilisateurRequete.getId()))
        {
            Avis avis = AvisRequestDTO.fromDTOtoEntity(avisDTO, utilisateur);
            return this.avisRepository.save(avis);
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Les utilisateurs ne correspondent pas");
        }

    }

    public List<Avis> liste()
    {
        return (List<Avis>) this.avisRepository.findAll();
    }
}
