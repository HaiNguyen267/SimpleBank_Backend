package hainguyen.tech.SimpleBank.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String token;

    @OneToOne
    private AppUser appUser;

    public VerificationToken(AppUser appUser) {
        this.token = UUID.randomUUID().toString();
        this.appUser = appUser;
    }
}
