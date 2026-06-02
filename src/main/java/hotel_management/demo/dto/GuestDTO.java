package hotel_management.demo.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GuestDTO {
  private UUID id;
  private String firstName;
  private String lastName;
  private String address;
  private String origin;
  private String phone;
  private UUID accountId;
  private String username;
  private String email;
  private String idNumber;
  private LocalDateTime dob;
  private String role;
  private LocalDateTime createdAt;
}
