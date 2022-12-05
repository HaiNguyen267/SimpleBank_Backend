package hainguyen.tech.SimpleBank.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import hainguyen.tech.SimpleBank.dto.response.TransactionDTO;
import hainguyen.tech.SimpleBank.security.AuthenticationProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;
import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;
import static hainguyen.tech.SimpleBank.entity.Transaction.TransactionType.*;

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
    @OneToMany(cascade =  CascadeType.REMOVE)
    private List<Transaction> transactions;


    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        boolean isOutTransaction = checkIsOutTransaction(transaction);
        if (isOutTransaction) {
            balance -= transaction.getAmount();
        } else {
            balance += transaction.getAmount();
        }
    }

    private boolean checkIsOutTransaction(Transaction transaction) {
        return Objects.equals(accountNo, transaction.getSenderAccountNo());
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


            Transaction.TransactionType type = identifyTransactionType(transaction); // deposit, withdraw, in or out
            String targetAccountNo = identifyTransactionAccountNo(transaction); // the account no where the transaction is sent to or sent from
            String targetAccountName = identifyTransactionAccountName(transaction); // the account name where the transaction is sent to or sent from

            TransactionDTO transactionDTO = TransactionDTO.builder()
                    .accountNo(targetAccountNo) // deposit and withdraw transaction don't have target account no
                    .accountName(targetAccountName) // deposit and withdraw transaction don't have target account name
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


    private Transaction.TransactionType identifyTransactionType(Transaction transaction) {
        Transaction.TransactionType type;
        // out transaction can be transaction to other account or withdraw
        if (transaction.getSenderAccountNo().equals(accountNo)) {
            if (transaction.getReceiver() == null) {
                type = WITHDRAW;
            } else {
                type = OUT;
            }
        } else {
            // in transaction can be transaction from other account or deposit
            if (transaction.getSender() == null) {
                type = DEPOSIT;
            } else {
                type = IN;
            }
        }
        return type;
    }

    private String identifyTransactionAccountNo(Transaction transaction) {

        Transaction.TransactionType type = identifyTransactionType(transaction);

        if (type == OUT) {
            return transaction.getReceiverAccountNo();
        } else if (type == IN) {
            return transaction.getSenderAccountNo();
        } else {
            return null;
        }
    }

    private String identifyTransactionAccountName(Transaction transaction) {

        Transaction.TransactionType type = identifyTransactionType(transaction);

        if (type == OUT) {
            return transaction.getReceiverAccountName();
        } else if (type == IN) {
            return transaction.getSenderAccountName();
        } else {
            return null;
        }
    }
}
