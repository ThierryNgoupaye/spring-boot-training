package tech.formation.springSecurityTraining.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import tech.formation.springSecurityTraining.entite.Jwt;

import java.util.Optional;

public interface JwtRepository extends CrudRepository<Jwt, Integer> {
    Optional<Jwt> findByValue(String value);

    @Query("FROM Jwt j WHERE j.expire = :expire AND j.desactive= :desactive AND j.utilisateur.email=:email")
    Optional<Jwt>findUtilisateurValidToken(String email, boolean desactive, boolean expire);
}
