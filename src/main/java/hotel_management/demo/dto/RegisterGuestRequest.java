package hotel_management.demo.dto;

import lombok.*;

@Getter
@Setter
public class RegisterGuestRequest {
  private String username;
  private String password;
  private String firstName;
  private String lastName;
  private String email;
  private String phone;
}
