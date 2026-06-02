package hotel_management.demo.service;

import java.util.List;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import hotel_management.demo.constant.Role;
import hotel_management.demo.dto.AuthResponse;
import hotel_management.demo.dto.RegisterGuestRequest;
import hotel_management.demo.repository.AccountRepository;
import hotel_management.demo.repository.GuestRepository;
import hotel_management.demo.schema.Account;
import hotel_management.demo.schema.Guest;

@Service
public class GuestService {

  private final PasswordEncoder passwordEncoder;

  private final GuestRepository guestRepository;
  private final AccountRepository accountRepository;
  private final AccountService accountService;

  public GuestService(
      GuestRepository guestRepository,
      AccountRepository accountRepository,
      AccountService accountService, PasswordEncoder passwordEncoder) {
    this.guestRepository = guestRepository;
    this.accountRepository = accountRepository;
    this.accountService = accountService;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public Guest createGuest(Guest guest) {
    Account account = guest.getAccount();

    if (account == null) {
      account = new Account();
      accountService.initializeAccountDefaults(account, Role.GUEST);
      account = accountService.createAccount(account);
    } else {
      if (account.getRole() == null) {
        account.setRole(Role.GUEST);
      }
      if (account.getIsActive() == null) {
        account.setIsActive(true);
      }
      if (account.getPassword() != null) {
        account.setPassword(passwordEncoder.encode(account.getPassword()));
      }
      account = accountRepository.save(account);
    }

    guest.setAccount(account);

    return guestRepository.save(guest);
  }

  public AuthResponse registerGuest(RegisterGuestRequest req) {
    Account account = new Account();
    account.setUsername(req.getUsername());
    account.setPassword(req.getPassword());
    account.setEmail(req.getEmail());

    Guest guest = new Guest();
    guest.setFirstName(req.getFirstName());
    guest.setLastName(req.getLastName());
    guest.setPhone(req.getPhone());
    guest.setAccount(account);

    guest = createGuest(guest);

    AuthResponse response = new AuthResponse();
    response.setMessage("Guest registered successfully");
    response.setRole(Role.GUEST);
    response.setGuestId(guest.getId());
    response.setToken("dummy");

    return response;
  }

  public Guest getGuestById(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("Guest ID cannot be null");
    }
    return guestRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Guest not found with ID: " + id));
  }

  public List<Guest> getAllGuests() {
    return guestRepository.findAll();
  }

  public Guest getGuestByAccountId(UUID id) {
    return guestRepository.findByAccountId(id);
  }

  @Transactional
  public Guest updateGuest(UUID id, Guest guestDetails) {
    Guest existingGuest = getGuestById(id);

    existingGuest.setFirstName(guestDetails.getFirstName());
    existingGuest.setLastName(guestDetails.getLastName());
    existingGuest.setPhone(guestDetails.getPhone());
    existingGuest.setAddress(guestDetails.getAddress());
    existingGuest.setOrigin(guestDetails.getOrigin());

    if (guestDetails.getAccount() != null && existingGuest.getAccount() != null) {

      Account existingAccount = existingGuest.getAccount();
      if (existingAccount == null) {
        throw new EntityNotFoundException("Account not found for Guest ID: " + id);
      }
      Account newAccount = guestDetails.getAccount();

      if (newAccount.getEmail() != null) {
        existingAccount.setEmail(newAccount.getEmail());
      }
      if (newAccount.getUsername() != null) {
        existingAccount.setUsername(newAccount.getUsername());
      }
      // if (newAccount.getPassword() != null) {
      //   existingAccount.setPassword(passwordEncoder.encode(newAccount.getPassword()));
      // }
      if (newAccount.getDob() != null) {
        existingAccount.setDob(newAccount.getDob());
        
      }
      if (newAccount.getIdNumber() != null) {
        existingAccount.setIdNumber(newAccount.getIdNumber());
      }
      accountRepository.save(existingAccount);
    }

    return guestRepository.save(existingGuest);
  }

  public Guest updateGuestAccount(UUID guestId, Account accountDetails) {
    Guest existingGuest = getGuestById(guestId);
    Account existingAccount = existingGuest.getAccount();

    if (existingAccount == null) {
      throw new EntityNotFoundException("Account not found for Guest ID: " + guestId);
    }

    if (accountDetails.getEmail() != null) {
      existingAccount.setEmail(accountDetails.getEmail());
    }
    if (accountDetails.getUsername() != null) {
      existingAccount.setUsername(accountDetails.getUsername());
    }
    if (accountDetails.getPassword() != null) {
      existingAccount.setPassword(accountDetails.getPassword());
    }

    accountRepository.save(existingAccount);
    return existingGuest;
  }

  @Transactional
  public void deleteGuest(UUID id) {
    Guest guest = getGuestById(id);
    UUID accountId = guest.getAccount().getId();

    if (accountId == null) {
      throw new EntityNotFoundException("Account not found for Guest ID: " + id);
    }

    guestRepository.delete(guest);
    accountRepository.deleteById(accountId);
  }
}
