package account.log;

import lombok.*;
import account.pojo.enums.LogEvent;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    private LogEvent action;

    // User email
    @Column(name = "subject", nullable = false)
    private String subject;

    // Event information
    @Column(name = "object", nullable = false)
    private String object;

    // Path where the action was
    @Column(name = "path", nullable = false)
    private String path;
}
