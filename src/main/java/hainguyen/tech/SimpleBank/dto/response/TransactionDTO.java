package hainguyen.tech.SimpleBank.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import hainguyen.tech.SimpleBank.entity.AppUser;
import hainguyen.tech.SimpleBank.entity.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDTO {

    @Enumerated(value = EnumType.STRING)
    private Transaction.TransactionType type;

    private int amount;
    private String message;
    private String date;
    private String accountNo; // the other people account no, who current user send money to or receive money from
    private String accountName; // teh other people account name, who current user send money to or receive money from

}
