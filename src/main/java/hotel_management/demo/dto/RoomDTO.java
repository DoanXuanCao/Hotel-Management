package hotel_management.demo.dto;

import java.util.UUID;

import hotel_management.demo.constant.RoomStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoomDTO {
  private UUID id;
  private Integer floor;
  private String roomNumber;
  private String note;
  private RoomStatus status;
  private UUID hotelId;
  private String hotelName;
  private UUID roomTypeId;
  private String roomTypeName;
}
