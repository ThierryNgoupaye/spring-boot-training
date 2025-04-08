package tech.formation.springSecurityTraining.DTO;


import lombok.*;



@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T>{

    private String status;
    private T data;
    private String description;
}
