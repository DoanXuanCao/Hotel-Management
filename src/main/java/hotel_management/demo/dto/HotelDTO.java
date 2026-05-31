package hotel_management.demo.dto;

import java.util.List;
import java.util.UUID;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HotelDTO {
  private UUID id;
  private String name;
  private Double rating;
  private String phone;
  private String email;
  private String address;
  private List<RoomDTO> rooms;
  private List<RoomTypeDTO> roomTypes;
  private List<EmployeeDTO> employees;
}
