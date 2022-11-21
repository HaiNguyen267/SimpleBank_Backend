package hainguyen.tech.SimpleBank.service;

import hainguyen.tech.SimpleBank.dto.*;
import hainguyen.tech.SimpleBank.dto.request.AccountNameRequest;
import hainguyen.tech.SimpleBank.dto.response.AccountNameResponse;
import hainguyen.tech.SimpleBank.dto.response.UserInfoResponseDTO;
import hainguyen.tech.SimpleBank.entity.AppUser;
import hainguyen.tech.SimpleBank.entity.Transaction;
import hainguyen.tech.SimpleBank.exception.AccountNoNotFound;
import hainguyen.tech.SimpleBank.repository.AppUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
@AllArgsConstructor
public class AppUserService  {

    private final AppUserRepository appUserRepository;
    private final TransactionService transactionService;


    public ResponseEntity<?> handleDepositRequest(DepositRequest depositRequest, String email) {
        AppUser appUser = getAppUserByEmail(email);
        appUser.setBalance(appUser.getBalance() + depositRequest.getAmount());
        appUserRepository.save(appUser);

        UserInfoResponseDTO response = new UserInfoResponseDTO(appUser, "Deposit successfully!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<?> handleWithDrawRequest(WithdrawRequest withdrawRequest,  String email) {
        AppUser appUser = getAppUserByEmail(email);
        if (appUser.getBalance() < withdrawRequest.getAmount()) {
            CustomResponseDTO response = new CustomResponseDTO(false, "Not enough money");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        appUser.setBalance(appUser.getBalance() - withdrawRequest.getAmount());
        appUser = appUserRepository.save(appUser);

        UserInfoResponseDTO response = new UserInfoResponseDTO(appUser, "Withdraw successfully!");
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    public ResponseEntity<?> handleTransferRequest(TransferRequest transferRequest, String email) {
        AppUser sender = getAppUserByEmail(email);


        if (sender.getBalance() <  transferRequest.getAmount()) {
            CustomResponseDTO response = new CustomResponseDTO(false, "Not enough money");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        AppUser receiver = getAppUserByAccountNo(transferRequest.getReceiverAccountNo());
        if (sender.getAccountNo().equals(receiver.getAccountNo())) {
            CustomResponseDTO response = new CustomResponseDTO(false, "Cannot transfer to yourself");
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
        UserInfoResponseDTO response = new UserInfoResponseDTO(sender, "Transfer successfully!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<?> handleGetTransactions(String email) {
        AppUser appUser = getAppUserByEmail(email);
        List<TransactionDTO> transactions = appUser.getTransactions();
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    private AppUser getAppUserByAccountNo(String accountNo) {
        return appUserRepository.findByAccountNo(accountNo)
                .orElseThrow(() -> new AccountNoNotFound("Account number not found"));
    }

    public List<AppUser> getAllAppUsers() {
        return appUserRepository.findAll();
    }

    public void save(AppUser appUser) {
        appUserRepository.save(appUser);
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
}


