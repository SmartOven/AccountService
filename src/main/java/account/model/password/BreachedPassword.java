package account.model.password;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "breached_passwords")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BreachedPassword {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "password", nullable = false, unique = true)
    private String password;
}
