package hainguyen.tech.SimpleBank.controller;

import hainguyen.tech.SimpleBank.dto.request.DeleteUserRequest;
import hainguyen.tech.SimpleBank.dto.response.AppUserDTO;
import hainguyen.tech.SimpleBank.entity.AppUser;
import hainguyen.tech.SimpleBank.service.AppUserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@AllArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final AppUserService appUserService;


    @GetMapping("/users")
    public List<AppUserDTO> getUsers() {
        return appUserService.getAllAppUsers();
    }

    @DeleteMapping("/user/{accountNo}/delete")
    public ResponseEntity<?> deleteUserByAccountNo(@PathVariable String accountNo) {
        return appUserService.deleteUserByAccountNo(accountNo);
    }

    @GetMapping("/user/{accountNo}/details")
    public AppUser getUserDetailsByAccountNo(@PathVariable String accountNo) {
        return appUserService.getAppUserByAccountNo(accountNo);
    }
}
