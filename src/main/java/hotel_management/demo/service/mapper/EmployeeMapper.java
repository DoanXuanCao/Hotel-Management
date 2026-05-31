package hotel_management.demo.service.mapper;

import org.springframework.stereotype.Component;

import hotel_management.demo.dto.EmployeeDTO;
import hotel_management.demo.schema.Employee;

@Component
public class EmployeeMapper {
    public EmployeeDTO toDTO(Employee employee) {
        if (employee == null) return null;

        return new EmployeeDTO(
            employee.getId(),
            employee.getFirstName(),
            employee.getLastName(),
            employee.getPhone(),
            employee.getPosition(),
            employee.getHireDate(),
            employee.getSalary(),
            employee.getHotel().getId(),
            employee.getHotel().getName(),
            employee.getAccount().getId(),
            employee.getAccount().getUsername(),
            employee.getAccount().getEmail(),
            employee.getAccount().getIdNumber(),
            employee.getAccount().getDob(),
            employee.getAccount().getRole(),
            employee.getAccount().getCreatedAt()
       );
    }
}
