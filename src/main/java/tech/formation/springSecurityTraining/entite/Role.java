package tech.formation.springSecurityTraining.entite;


import jakarta.persistence.*;
import lombok.*;
import tech.formation.springSecurityTraining.enumeration.TypeDeRole;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name= "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Enumerated(EnumType.STRING)
    private TypeDeRole libelle;
}
