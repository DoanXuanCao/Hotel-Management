package hotel_management.demo.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;
import hotel_management.demo.dto.EmployeeDTO;
import hotel_management.demo.schema.Employee;
import hotel_management.demo.service.EmployeeService;
import hotel_management.demo.service.mapper.EmployeeMapper;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

  private final EmployeeMapper employeeMapper;

  private final EmployeeService employeeService;

  public EmployeeController(EmployeeService employeeService, EmployeeMapper employeeMapper) {
    this.employeeMapper = employeeMapper;
    this.employeeService = employeeService;
  }

  @PostMapping
  public ResponseEntity<EmployeeDTO> createEmployee(@RequestBody Employee employee) {
    Employee createdEmployee = employeeService.createEmployee(employee);
    EmployeeDTO employeeDTO = employeeMapper.toDTO(createdEmployee);
    return new ResponseEntity<>(employeeDTO, HttpStatus.CREATED);
  }

  @GetMapping("/")
  public ResponseEntity<List<EmployeeDTO>> getAllEmployee() {
    List<Employee> employees = employeeService.getAllEmployees();
    List<EmployeeDTO> employeeDTOs = employees.stream()
        .map(employeeMapper::toDTO)
        .collect(Collectors.toList());
    return ResponseEntity.ok(employeeDTOs);
  }

  @GetMapping("/{id}")
  public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable UUID id) {
    try {
      Employee employee = employeeService.getEmployeeById(id);
      EmployeeDTO employeeDTO = employeeMapper.toDTO(employee);

      return ResponseEntity.ok(employeeDTO);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<EmployeeDTO> updateEmployee(@PathVariable UUID id, @RequestBody Employee employee) {
    try {
      Employee updatedEmployee = employeeService.updateEmployee(id, employee);
      return ResponseEntity.ok(employeeMapper.toDTO(updatedEmployee));
    } catch (EntityNotFoundException e) {
      System.err.println("Employee not found for update: " + id + ". Error: " + e.getMessage());
      return ResponseEntity.notFound().build();
    } catch (IllegalArgumentException e) {
      System.err.println("Bad request during employee update: " + e.getMessage());
      return ResponseEntity.badRequest().build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteEmployee(@PathVariable UUID id) {
    employeeService.deleteEmployee(id);
    return ResponseEntity.noContent().build();
  }
}