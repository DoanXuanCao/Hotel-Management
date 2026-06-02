package hotel_management.demo.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;

import hotel_management.demo.constant.Role;
import hotel_management.demo.repository.EmployeeRepository;
import hotel_management.demo.schema.Account;
import hotel_management.demo.service.AccountService;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
  private final AccountService accountService;
  private final EmployeeRepository employeeRepository;

  public AccountController(AccountService accountService, EmployeeRepository employeeRepository) {
    this.accountService = accountService;
    this.employeeRepository = employeeRepository;
  }

  // Trả về danh sách account role=EMPLOYEE không có Employee record (orphaned)
  @GetMapping("/orphaned")
  public ResponseEntity<List<java.util.Map<String, Object>>> getOrphanedAccounts() {
    List<java.util.Map<String, Object>> result = accountService.getAllAccounts().stream()
        .filter(acc -> acc.getRole() == Role.EMPLOYEE
            && employeeRepository.findByAccountId(acc.getId()) == null)
        .map(acc -> {
          java.util.Map<String, Object> map = new java.util.LinkedHashMap<>();
          map.put("id", acc.getId());
          map.put("username", acc.getUsername());
          map.put("email", acc.getEmail());
          map.put("role", acc.getRole());
          map.put("createdAt", acc.getCreatedAt());
          return map;
        })
        .collect(java.util.stream.Collectors.toList());
    return ResponseEntity.ok(result);
  }

  @PostMapping
  public ResponseEntity<?> createAccount(@RequestBody Account account) {
    // Chặn tạo EMPLOYEE/ADMIN trực tiếp — phải dùng /api/employees hoặc /api/auth/register
    if (account.getRole() != null &&
        (account.getRole().name().equals("EMPLOYEE") || account.getRole().name().equals("ADMIN"))) {
      return ResponseEntity.badRequest()
          .body(java.util.Map.of("message",
              "Cannot create EMPLOYEE/ADMIN account directly. Use /api/employees instead."));
    }
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