package hotel_management.demo.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;

import hotel_management.demo.schema.Account;
import hotel_management.demo.service.AccountService;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
  private final AccountService accountService;

  public AccountController(AccountService accountService) {
    this.accountService = accountService;
  }

  @PostMapping
  public ResponseEntity<Account> createAccount(@RequestBody Account account) {
    try {
      Account createdAccount = accountService.createAccount(account);
      return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @GetMapping
  public ResponseEntity<List<Account>> getAllAccounts() {
    return ResponseEntity.ok(accountService.getAllAccounts());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Account> getAccountById(@PathVariable UUID id) {
    try {
      return ResponseEntity.ok(accountService.getAccountById(id));
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<Account> updateAccount(@PathVariable UUID id, @RequestBody Account details) {
    try {
      return ResponseEntity.ok(accountService.updateAccount(id, details));
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteAccount(@PathVariable UUID id) {
    try {
      accountService.deleteAccount(id);
      return ResponseEntity.noContent().build();
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }
}