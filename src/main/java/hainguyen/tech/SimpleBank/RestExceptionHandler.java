package hainguyen.tech.SimpleBank;

import hainguyen.tech.SimpleBank.dto.CustomResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleUsernameNotFoundException(UsernameNotFoundException e) {
        CustomResponseDTO customResponseDTO = new CustomResponseDTO(false, e.getMessage());
        return new ResponseEntity<>(customResponseDTO, HttpStatus.NOT_FOUND);

    }
}
