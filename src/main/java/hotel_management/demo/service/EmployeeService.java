package hotel_management.demo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import hotel_management.demo.constant.Role;
import hotel_management.demo.repository.AccountRepository;
import hotel_management.demo.repository.EmployeeRepository;
import hotel_management.demo.schema.Account;
import hotel_management.demo.schema.Employee;
import hotel_management.demo.schema.Hotel;

@Service
public class EmployeeService {

  private final PasswordEncoder passwordEncoder;

  private final EmployeeRepository employeeRepository;
  private final AccountRepository accountRepository;
  private final HotelService hotelService;
  private final AccountService accountService;

  public EmployeeService(EmployeeRepository employeeRepository, AccountRepository accountRepository,
      HotelService hotelService, AccountService accountService, PasswordEncoder passwordEncoder) {
    this.employeeRepository = employeeRepository;
    this.accountRepository = accountRepository;
    this.hotelService = hotelService;
    this.accountService = accountService;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public Employee createEmployee(Employee employee) {
    Account account = employee.getAccount();
    if (account == null) {
      account = new Account();
      accountService.initializeAccountDefaults(account, Role.EMPLOYEE);
      accountService.createAccount(account);
      account = accountRepository.save(account);
    } else {
      if (account.getRole() == null)
        account.setRole(Role.EMPLOYEE);
      if (account.getIsActive() == null)
        account.setIsActive(true);
      if (account.getPassword() != null) {
        account.setPassword(passwordEncoder.encode(account.getPassword()));
      }
      account = accountRepository.save(account);
    }

    if (employee.getPosition() == null) {
      employee.setPosition("Receptionist");
    }

    if (employee.getSalary() == null) {
      employee.setSalary(1000000);
    }

    if (employee.getHireDate() == null) {
      employee.setHireDate(LocalDate.now());
    }

    if (employee.getHotel() != null && employee.getHotel().getId() != null) {

      Hotel existingHotel = hotelService.getHotelById(employee.getHotel().getId());
      employee.setHotel(existingHotel);
    } else {
      throw new RuntimeException("Employee must be assigned to a Hotel");
    }

    employee.setAccount(account);

    return employeeRepository.save(employee);
  }

  public Employee getEmployeeById(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("Employee ID cannot be null");
    }
    return employeeRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + id));
  }

  public List<Employee> getAllEmployees() {
    return employeeRepository.findAll();
  }

  public List<Employee> getEmployeeByHotelId(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("Hotel ID cannot be null");
    }
    return employeeRepository.getEmployeeByHotelId(id);
  }

  public Employee getEmployeeByAccountId (UUID id) {
    if(id == null) {
      throw new IllegalArgumentException("Account ID cannot be null");
    }
    return employeeRepository.findByAccountId(id);
  }

  @Transactional
  public Employee updateEmployee(UUID id, Employee employeeDetails) {
    Employee existingEmployee = getEmployeeById(id);
    Account existingAccount = accountService.getAccountById(existingEmployee.getAccount().getId());

    if (employeeDetails.getAccount() == null || employeeDetails.getAccount().getId() == null) {
      throw new IllegalArgumentException("Account details (including ID) must be provided for update.");
    }
    if (employeeDetails.getFirstName() != null) {
      existingEmployee.setFirstName(employeeDetails.getFirstName());
    }
    if (employeeDetails.getLastName() != null) {
      existingEmployee.setLastName(employeeDetails.getLastName());
    }
    if (employeeDetails.getPhone() != null) {
      existingEmployee.setPhone(employeeDetails.getPhone());
    }
    if (employeeDetails.getPosition() != null) {
      existingEmployee.setPosition(employeeDetails.getPosition());
    }
    if (employeeDetails.getHireDate() != null) {
      existingEmployee.setHireDate(employeeDetails.getHireDate());
    }
    if (employeeDetails.getSalary() != null) {
      existingEmployee.setSalary(employeeDetails.getSalary());
    }

    if (employeeDetails.getHotel() != null && employeeDetails.getHotel().getId() != null) {
      existingEmployee.setHotel(employeeDetails.getHotel());
    }
    Account updatedAccountDetails = employeeDetails.getAccount();

    if (updatedAccountDetails != null) {
      if (updatedAccountDetails.getUsername() != null) {
        existingAccount.setUsername(updatedAccountDetails.getUsername());
      }
      if (updatedAccountDetails.getEmail() != null) {
        existingAccount.setEmail(updatedAccountDetails.getEmail());
      }
      if (updatedAccountDetails.getIdNumber() != null) {
        existingAccount.setIdNumber(updatedAccountDetails.getIdNumber());
      }
      if (updatedAccountDetails.getDob() != null) {
        existingAccount.setDob(updatedAccountDetails.getDob());
      }
      if (updatedAccountDetails.getPassword() != null && !updatedAccountDetails.getPassword().isEmpty()) {
        existingAccount.setPassword(passwordEncoder.encode(updatedAccountDetails.getPassword()));
      }
      accountRepository.save(existingAccount);
    }
    return employeeRepository.save(existingEmployee);
  }

  public void deleteEmployee(UUID id) {
    Employee employee = getEmployeeById(id);
    UUID accountId = employee.getAccount().getId();

    if (accountId == null) {
      throw new EntityNotFoundException("Account not found for Guest ID: " + id);
    }

    employeeRepository.delete(employee);
    accountRepository.deleteById(accountId);
  }
}
