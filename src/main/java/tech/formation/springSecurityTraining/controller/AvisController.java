package tech.formation.springSecurityTraining.controller;


import io.jsonwebtoken.ExpiredJwtException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import tech.formation.springSecurityTraining.entite.Avis;
import tech.formation.springSecurityTraining.service.AvisService;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping
public class AvisController {

    private final AvisService avisService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "avis", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void creer(@RequestBody Avis avis)
    {
        try
        {
            log.info("creation de l'avis");
            this.avisService.creer(avis);

        }
        catch (ExpiredJwtException e)
        {
            log.error(e.getMessage());

        }

    }


}
