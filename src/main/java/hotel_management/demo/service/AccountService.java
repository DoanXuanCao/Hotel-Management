package hotel_management.demo.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.persistence.EntityNotFoundException;

import hotel_management.demo.repository.AccountRepository;
import hotel_management.demo.repository.GuestRepository;
import hotel_management.demo.repository.EmployeeRepository;
import hotel_management.demo.schema.Account;
import hotel_management.demo.schema.Employee;
import hotel_management.demo.schema.Guest;
import hotel_management.demo.constant.Role;
import hotel_management.demo.dto.AuthResponse;
import hotel_management.demo.dto.LoginRequest;

@Service
public class AccountService {

  private final AccountRepository accountRepository;
  private final PasswordEncoder passwordEncoder;
  private final GuestRepository guestRepository;
  private final EmployeeRepository employeeRepository;

  public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder, GuestRepository guestRepository,
      EmployeeRepository employeeRepository) {
    this.accountRepository = accountRepository;
    this.passwordEncoder = passwordEncoder;
    this.employeeRepository = employeeRepository;
    this.guestRepository = guestRepository;
  }

  public Account createAccount(Account account) {
    account.setIsActive(true);

    System.out.println(account);

    if (account.getPassword() != null) {
      account.setPassword(passwordEncoder.encode(account.getPassword()));
    }

    return accountRepository.save(account);
  }

  public Account getAccountById(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("Account ID cannot be null");
    }

    return accountRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Account not found with ID: " + id));
  }

  public List<Account> getAllAccounts() {
    return accountRepository.findAll();
  }

  public Account updateAccount(UUID id, Account details) {
    Account existing = getAccountById(id);
    if (existing == null) {
      throw new EntityNotFoundException("Account not found with ID: " + id);
    }

    if (details.getUsername() != null) {
      existing.setUsername(details.getUsername());
    }

    if (details.getEmail() != null) {
      existing.setEmail(details.getEmail());
    }

    if (details.getPassword() != null && !details.getPassword().isBlank()) {
      existing.setPassword(passwordEncoder.encode(details.getPassword()));
    }

    if (details.getIdNumber() != null) {
      existing.setIdNumber(details.getIdNumber());
    }

    if (details.getRole() != null) {
      existing.setRole(details.getRole());
    }

    if (details.getDob() != null) {
      existing.setDob(details.getDob());
    }

    if (details.getIsActive() != null) {
      existing.setIsActive(details.getIsActive());
    }

    return accountRepository.save(existing);
  }

  public void deleteAccount(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("Account ID cannot be null");
    }

    if (!accountRepository.existsById(id)) {
      throw new EntityNotFoundException("Account not found with ID: " + id);
    }

    accountRepository.deleteById(id);
  }

  public void initializeAccountDefaults(Account account, Role role) {
    if (account.getIdNumber() == null) {
      account.setIdNumber(UUID.randomUUID().toString());
    }
    if (account.getUsername() == null) {
      account.setUsername("guest_" + UUID.randomUUID().toString().substring(0, 8));
    }
    if (account.getEmail() == null) {
      account.setEmail(account.getUsername() + "@example.com");
    }
    if (account.getPassword() == null) {
      account.setPassword(passwordEncoder.encode("defaultPassword"));

    }
    if (account.getIsActive() == null) {
      account.setIsActive(true);
    }
    if (account.getRole() == null) {
      account.setRole(role);
    }
  }

  // Authentication
  public AuthResponse login(LoginRequest request) {
    Account acc = accountRepository.findByUsername(request.getUsername())
        .orElseThrow(() -> new RuntimeException("Incorrect username or password"));

    if (!passwordEncoder.matches(request.getPassword(), acc.getPassword())) {
      throw new RuntimeException("Incorrect username or password");
    }

    AuthResponse response = new AuthResponse();
    response.setMessage("Login successful");
    response.setRole(acc.getRole());
    response.setToken("dummy");

    if (acc.getRole() == Role.EMPLOYEE) {
      Employee employee = employeeRepository.findByAccountId(acc.getId());
      if (employee == null) {
        throw new RuntimeException(
            "Account '" + acc.getUsername() + "' is misconfigured: no Employee record linked. " +
            "Please ask an admin to delete this account and recreate it via the Setting page.");
      }
      response.setEmployeeId(employee.getId());
    }

    if (acc.getRole() == Role.GUEST) {
      Guest guest = guestRepository.findByAccountId(acc.getId());
      if (guest == null) {
        throw new RuntimeException("Account '" + acc.getUsername() + "' is misconfigured: no Guest record linked.");
      }
      response.setGuestId(guest.getId());
    }

    return response;
  }

  public AuthResponse refreshToken(String oldToken) {
    AuthResponse r = new AuthResponse();
    r.setMessage("Token refreshed");
    r.setToken("new-token");
    return r;
  }
}
