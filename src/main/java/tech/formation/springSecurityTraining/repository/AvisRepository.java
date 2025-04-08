package tech.formation.springSecurityTraining.repository;

import org.springframework.data.repository.CrudRepository;
import tech.formation.springSecurityTraining.entite.Avis;

public interface AvisRepository extends CrudRepository<Avis, Integer> {
}
