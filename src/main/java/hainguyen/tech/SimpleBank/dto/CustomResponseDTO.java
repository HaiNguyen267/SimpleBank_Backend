package hainguyen.tech.SimpleBank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomResponseDTO {
    private boolean success;
    private String message;
}
