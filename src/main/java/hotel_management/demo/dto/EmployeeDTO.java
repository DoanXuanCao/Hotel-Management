package hotel_management.demo.dto;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.UUID;

import lombok.*;

import hotel_management.demo.constant.Role;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDTO {
  private UUID id;
  private String firstname;
  private String lastname;
  private String phone;
  private String position;
  private LocalDate hireDate;
  private Integer salary;
  private UUID hotelId;
  private String hotelName;
  private UUID accountId;
  private String username;
  private String email;
  private String idNumber;
  private LocalDateTime dob;
  private Role role;
  private LocalDateTime createdAt;
}
