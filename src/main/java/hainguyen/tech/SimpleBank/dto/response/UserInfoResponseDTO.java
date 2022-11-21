package hainguyen.tech.SimpleBank.dto.response;

import hainguyen.tech.SimpleBank.entity.AppUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfoResponseDTO {
    private boolean success;
    private String message;
    private String accountNo;
    private String fullName;
    private boolean isEnabled;
    private int balance;
    private String profileImage;

    public UserInfoResponseDTO(AppUser appUser, String message) {
        this.success = true;
        this.message = message;
        this.accountNo = appUser.getAccountNo();
        this.fullName = appUser.getFullName();
        this.isEnabled = appUser.isEnabled();
        this.balance = appUser.getBalance();
        this.profileImage = appUser.getProfileImage();
    }
}
