package hotel_management.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

import hotel_management.demo.constant.Role;

@Getter
@Setter
public class AuthResponse {
  private String message;
  private String token;
  private Role role;
  private UUID employeeId;
  private UUID guestId;
}
