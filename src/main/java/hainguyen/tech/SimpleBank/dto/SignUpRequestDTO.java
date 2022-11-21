package hainguyen.tech.SimpleBank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequestDTO {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
}
