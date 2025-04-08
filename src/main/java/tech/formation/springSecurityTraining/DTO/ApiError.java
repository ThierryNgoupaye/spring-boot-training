package tech.formation.springSecurityTraining.DTO;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiError {

    private String status;
    private Object data;
    private String description;
}
