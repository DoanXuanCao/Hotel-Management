package hotel_management.demo.schema;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(name = "reservation_room")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ReservationRoom {

  @EmbeddedId
  private ReservationRoomId id = new ReservationRoomId();

  @ManyToOne
  @MapsId("roomId")
  @JoinColumn(name = "room_id", nullable = false)
  private Room room;

  @ManyToOne
  @MapsId("reservationId")
  @JoinColumn(name = "reservation_id", nullable = false)
  private Reservation reservation;
}
