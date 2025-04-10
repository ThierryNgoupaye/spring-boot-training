package tech.formation.springSecurityTraining.controller;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.formation.springSecurityTraining.DTO.ApiResponse;
import tech.formation.springSecurityTraining.DTO.responseDTO.AvisResponseDTO;
import tech.formation.springSecurityTraining.DTO.resquestDTO.AvisRequestDTO;
import tech.formation.springSecurityTraining.entite.Avis;
import tech.formation.springSecurityTraining.service.AvisService;


import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(path = "avis", produces = MediaType.APPLICATION_JSON_VALUE)
public class AvisController {

    private final AvisService avisService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<AvisResponseDTO>> creer(@RequestBody AvisRequestDTO avisRequestDTO)
    {
        log.info("Création de l'avis");

        Avis nouvelAvis = this.avisService.creer(avisRequestDTO);
        AvisResponseDTO avisResponseDTO = AvisResponseDTO.fromEntityToDTO(nouvelAvis);

        ApiResponse<AvisResponseDTO> apiResponseDTO = ApiResponse.<AvisResponseDTO>builder()
                .data(avisResponseDTO)
                .description("Avis créé avec succès")
                .status(String.valueOf(HttpStatus.CREATED.value()))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponseDTO);
    }



    @GetMapping
    public ResponseEntity<ApiResponse<List<AvisResponseDTO>>> liste() {
        List<AvisResponseDTO> listAvis = this.avisService.liste()
                .stream()
                .map(AvisResponseDTO::fromEntityToDTO)
                .collect(Collectors.toList());

        ApiResponse<List<AvisResponseDTO>> apiResponse = ApiResponse.<List<AvisResponseDTO>>builder()
                .status(String.valueOf(HttpStatus.OK.value()))
                .description("Liste des avis récupérée avec succès")
                .data(listAvis)
                .build();

        return ResponseEntity.ok(apiResponse);
    }





}
