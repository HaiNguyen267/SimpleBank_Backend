package hainguyen.tech.SimpleBank.service;

import hainguyen.tech.SimpleBank.dto.request.*;
import hainguyen.tech.SimpleBank.dto.response.*;
import hainguyen.tech.SimpleBank.entity.AppUser;
import hainguyen.tech.SimpleBank.entity.Transaction;
import hainguyen.tech.SimpleBank.exception.AccountNoNotFound;
import hainguyen.tech.SimpleBank.repository.AppUserRepository;
import hainguyen.tech.SimpleBank.security.JwtProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AppUserService  {

    private final AppUserRepository appUserRepository;
    private final TransactionService transactionService;
    private final JwtProvider jwtProvider;


    public ResponseEntity<?> handleDepositRequest(DepositRequest depositRequest, String email) {
        AppUser appUser = getAppUserByEmail(email);
        Transaction depositTransaction = transactionService.createTransaction(null, appUser, depositRequest.getAmount(), "Account owner deposit money");

//        appUser.setBalance(appUser.getBalance() + depositRequest.getAmount());
        appUser.addTransaction(depositTransaction);

        appUserRepository.save(appUser);
        transactionService.save(depositTransaction);

        UserInfoResponse response = new UserInfoResponse(appUser, "Deposit successfully!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<?> handleWithDrawRequest(WithdrawRequest withdrawRequest, String email) {
        AppUser appUser = getAppUserByEmail(email);
        if (appUser.getBalance() < withdrawRequest.getAmount()) {
            CustomResponse response = new CustomResponse(false, "Not enough money");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        Transaction withdrawTransaction = transactionService.createTransaction(appUser, null, withdrawRequest.getAmount(), "Account owner withdraw money");
//        appUser.setBalance(appUser.getBalance() - withdrawRequest.getAmount());
        appUser.addTransaction(withdrawTransaction);

        transactionService.save(withdrawTransaction);
        appUser = appUserRepository.save(appUser);

        UserInfoResponse response = new UserInfoResponse(appUser, "Withdraw successfully!");
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    public ResponseEntity<?> handleTransferRequest(TransferRequest transferRequest, String email) {
        AppUser sender = getAppUserByEmail(email);


        if (sender.getBalance() <  transferRequest.getAmount()) {
            CustomResponse response = new CustomResponse(false, "Not enough money");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        AppUser receiver = getAppUserByAccountNo(transferRequest.getReceiverAccountNo());
        if (sender.getAccountNo().equals(receiver.getAccountNo())) {
            CustomResponse response = new CustomResponse(false, "Cannot transfer to yourself");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        int amount = transferRequest.getAmount();
        String message = transferRequest.getMessage();
        Transaction transaction = transactionService.createTransaction(sender, receiver, amount, message);

        sender.addTransaction(transaction);
        receiver.addTransaction(transaction);

        transactionService.save(transaction);
        sender = appUserRepository.save(sender);
        receiver = appUserRepository.save(receiver);
        UserInfoResponse response = new UserInfoResponse(sender, "Transfer successfully!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<?> handleGetTransactions(String email) {
        AppUser appUser = getAppUserByEmail(email);
        List<TransactionDTO> transactions = appUser.getTransactions();
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    public AppUser getAppUserByAccountNo(String accountNo) {
        return appUserRepository.findByAccountNo(accountNo)
                .orElseThrow(() -> new AccountNoNotFound("Account number not found"));
    }

    public List<AppUserDTO> getAllAppUsers() {
        return appUserRepository.findAll()
                .stream()
                .map(AppUserDTO::new)
                .filter(appUserDTO -> !appUserDTO.getEmail().equals("admin")) // exclude the admin account
                .collect(Collectors.toList());
    }


    public AppUser getAppUserByEmail(String username) {
        return appUserRepository.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public ResponseEntity<?> getAccountName(AccountNameRequest accountNameRequest) {
        AppUser appUser = getAppUserByAccountNo(accountNameRequest.getAccountNo());
        AccountNameResponse response = new AccountNameResponse(true, appUser.getFullName());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    public ResponseEntity<?> deleteUserByAccountNo(String accountNo) {
        AppUser appUser = getAppUserByAccountNo(accountNo);
        appUserRepository.delete(appUser);
        CustomResponse response = new CustomResponse(true, "Delete user successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}


