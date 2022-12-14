package hainguyen.tech.SimpleBank.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
}
