package tech.formation.springSecurityTraining.DTO.responseDTO.Authentication;


import lombok.*;

import java.util.Date;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponseDTO {

    private String bearer;
    private Date expirationTime;

}
