package hotel_management.demo.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hotel_management.demo.schema.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
  List<Employee> getEmployeeByHotelId(UUID id);
  Employee findByAccountId(UUID id);
}