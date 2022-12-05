package hainguyen.tech.SimpleBank.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomResponse {
    private boolean success;
    private String message;
}
