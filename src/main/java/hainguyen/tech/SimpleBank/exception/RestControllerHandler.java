package hainguyen.tech.SimpleBank.exception;

import hainguyen.tech.SimpleBank.dto.response.CustomResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestControllerHandler {

    @ExceptionHandler(AccountNoNotFound.class)
    public ResponseEntity<?> handleAccountNoNotFound(AccountNoNotFound e) {
        CustomResponse response = new CustomResponse(false, "Account number not found");
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleUsernameNotFoundException(UsernameNotFoundException e) {
        CustomResponse customResponseDTO = new CustomResponse(false, e.getMessage());
        return new ResponseEntity<>(customResponseDTO, HttpStatus.NOT_FOUND);

    }
}
