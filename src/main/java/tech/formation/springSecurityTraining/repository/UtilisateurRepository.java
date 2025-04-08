package tech.formation.springSecurityTraining.repository;

import org.springframework.data.repository.CrudRepository;
import tech.formation.springSecurityTraining.entite.Utilisateur;

import java.util.Optional;

public interface UtilisateurRepository extends CrudRepository<Utilisateur, Integer> {
    Optional<Utilisateur> findByEmail(String email);
}
