package hotel_management.demo.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {
  private UUID id;
  private ReservationDTO reservation;
  private UUID guestId;
  private String guestName;
  private Integer amount;
  private String method;
  @JsonProperty("payment_date")
  private LocalDateTime paymentDate;
}
