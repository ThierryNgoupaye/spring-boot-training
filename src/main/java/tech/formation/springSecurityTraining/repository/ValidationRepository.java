package tech.formation.springSecurityTraining.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import tech.formation.springSecurityTraining.entite.Validation;

import java.util.Optional;

public interface ValidationRepository  extends CrudRepository<Validation, Integer>
{
    public Optional<Validation> findByCode(String code);
}
