package hainguyen.tech.SimpleBank.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {

    public enum TransactionType {
        IN, OUT, DEPOSIT, WITHDRAW
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int amount;
    private String message;
    private String date;
    @OneToOne
    private AppUser sender;
    @OneToOne
    private AppUser receiver;

    public String getSenderAccountNo() {
        return sender.getAccountNo();
    }

    public String getSenderAccountName() {
        return sender.getFullName();
    }

    public String getReceiverAccountNo() {
        return receiver.getAccountNo();
    }

    public String getReceiverAccountName() {
        return receiver.getFullName();
    }

 }
