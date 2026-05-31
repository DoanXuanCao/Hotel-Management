package hotel_management.demo.dto;

import java.util.UUID;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoomTypeDTO {
  private UUID id;
  private String name;
  private String description;
  private Integer capacity;
  private Integer basePrice;
}
