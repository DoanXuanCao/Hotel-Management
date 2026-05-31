package hotel_management.demo.dto;

import java.util.UUID;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationRoomDTO {
  private UUID id;
  private UUID roomId;
  private String roomNumber;
  private UUID hotelId;
  private String hotelName;
  private String status;
  private String note;
  private UUID roomTypeId;
}
