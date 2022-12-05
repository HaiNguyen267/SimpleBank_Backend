package hainguyen.tech.SimpleBank.dto.response;

import hainguyen.tech.SimpleBank.entity.AppUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppUserDTO {
    private String fullName;
    private String accountNo;
    private String email;
    private int balance;

    public AppUserDTO(AppUser appUser) {
        this.fullName = appUser.getFullName();
        this.accountNo = appUser.getAccountNo();
        this.email = appUser.getEmail();
        this.balance = appUser.getBalance();
    }
}
