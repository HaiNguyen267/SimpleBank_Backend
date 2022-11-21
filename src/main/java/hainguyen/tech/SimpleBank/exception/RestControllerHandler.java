package hainguyen.tech.SimpleBank.exception;

import hainguyen.tech.SimpleBank.dto.CustomResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestControllerHandler {

    @ExceptionHandler(AccountNoNotFound.class)
    public ResponseEntity<?> handleAccountNoNotFound(AccountNoNotFound e) {
        CustomResponseDTO response = new CustomResponseDTO(false, "Account number not found");
        return ResponseEntity.ok(response);
    }
}
