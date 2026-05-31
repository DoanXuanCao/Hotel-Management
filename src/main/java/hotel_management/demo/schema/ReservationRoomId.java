package hotel_management.demo.schema;

import java.io.Serializable;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

import lombok.*;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })

public class ReservationRoomId implements Serializable {

  @Column(name = "reservation_id")
  private UUID reservationId;

  @Column(name = "room_id")
  private UUID roomId;

  public ReservationRoomId() {
  }

  public ReservationRoomId(UUID reservationId, UUID roomId) {
    this.reservationId = reservationId;
    this.roomId = roomId;
  }
}