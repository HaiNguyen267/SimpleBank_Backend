package hainguyen.tech.SimpleBank.service;

import hainguyen.tech.SimpleBank.entity.AppUser;
import hainguyen.tech.SimpleBank.entity.Transaction;
import hainguyen.tech.SimpleBank.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static hainguyen.tech.SimpleBank.entity.Transaction.TransactionType.IN;
import static hainguyen.tech.SimpleBank.entity.Transaction.TransactionType.OUT;

@Service
@AllArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

//    public Transaction createInTransaction(AppUser sender, AppUser receiver, int amount, String message) {
//        Transaction transaction = createTransaction(sender, receiver, amount, message);
//        transaction.setType(IN);
//        return transaction;
//    }

    public Transaction createTransaction(AppUser sender, AppUser receiver, int amount, String message) {

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String formattedDate = formatter.format(LocalDateTime.now());
        return Transaction.builder()
                .amount(amount)
                .sender(sender)
                .receiver(receiver)
                .message(message)
                .date(formattedDate)
                .build();
    }

//    public Transaction createOutTransaction(AppUser sender, AppUser receiver, int amount, String message) {
//        Transaction transaction = createTransaction(sender, receiver, amount, message);
//        transaction.setType(OUT);
//        return transaction;
//    }

    public void saveAll(List<Transaction> transactions) {
        transactionRepository.saveAll(transactions);
    }

    public void save(Transaction transaction) {
        transactionRepository.save(transaction);
    }
}
