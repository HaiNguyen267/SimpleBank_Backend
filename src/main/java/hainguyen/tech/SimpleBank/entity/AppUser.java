package hainguyen.tech.SimpleBank.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import hainguyen.tech.SimpleBank.dto.TransactionDTO;
import hainguyen.tech.SimpleBank.security.AuthenticationProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;
import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;
import static hainguyen.tech.SimpleBank.entity.Transaction.TransactionType.IN;
import static hainguyen.tech.SimpleBank.entity.Transaction.TransactionType.OUT;

@Entity
@NamedQueries({
        @NamedQuery(name = "AppUser.findAll", query = "select a from AppUser a")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(value = {"id", "fullname", "roles" , "provider", "transactions"})
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String fullName;

    private String accountNo;

    @JsonProperty(access = WRITE_ONLY)
    private String firstname;

    @JsonProperty(access = WRITE_ONLY)
    private String lastname;

    private String email;

    @JsonProperty(access = WRITE_ONLY)
    private String password;

    private int balance;

    private String profileImage;

    private boolean enabled;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;

    @Enumerated(value = EnumType.STRING)
    private AuthenticationProvider provider;

    @JsonProperty(access = READ_ONLY)
    @ManyToMany(cascade =  CascadeType.REMOVE)
    private List<Transaction> transactions;


    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        boolean isOutTransaction = transaction.getSenderAccountNo().equals(accountNo);
        if (isOutTransaction) {
            balance -= transaction.getAmount();
        } else {
            balance += transaction.getAmount();
        }
    }

    public List<String> getRoles() {
        return roles.stream()
                .map(role -> "ROLE_" +role)
                .collect(Collectors.toList());
    }

    public String getFullName() {
        if (firstname == null || firstname.isBlank()) {
            return lastname;
        }
        if (lastname == null || lastname.isBlank()) {
            return firstname;
        }
        return firstname + " " + lastname;
    }


    public List<TransactionDTO> getTransactions() {
        List<TransactionDTO> transactionList = transactions.stream().map(transaction -> {
            // if the current appUser is the sender, then the transaction is out transaction, otherwise
            Transaction.TransactionType type
                    = transaction.getSenderAccountNo()
                    .equals(accountNo) ? OUT : IN;

            String accountNo = type == OUT ? transaction.getReceiverAccountNo() : transaction.getSenderAccountNo();
            String accountName = type == OUT ? transaction.getReceiverAccountName() : transaction.getSenderAccountName();
            TransactionDTO transactionDTO = TransactionDTO.builder()
                    .accountNo(accountNo)
                    .accountName(accountName)
                    .amount(transaction.getAmount())
                    .date(transaction.getDate())
                    .message(transaction.getMessage())
                    .type(type)
                    .build();

            return transactionDTO;
        }).collect(Collectors.toList());
        Collections.reverse(transactionList); // reverse the list, so that the latest transaction is on top
        return transactionList;
    }
}
