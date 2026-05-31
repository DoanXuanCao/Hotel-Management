package hotel_management.demo.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDTO {
  private UUID id;
  private UUID guestId;
  private String guestName;
  private LocalDateTime checkin;
  private LocalDateTime checkout;
  private String status;
  private List<ReservationRoomDTO> rooms;
  private UUID employeeId;
  private String employeeName;
}
