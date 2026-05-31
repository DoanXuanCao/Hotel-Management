package hotel_management.demo.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import hotel_management.demo.schema.ReservationRoom;
import hotel_management.demo.schema.ReservationRoomId;

@Repository
public interface ReservationRoomRepository extends JpaRepository<ReservationRoom, ReservationRoomId> {
  @Modifying
    @Query("DELETE FROM ReservationRoom rr WHERE rr.reservation.id = :reservationId")
    void deleteByReservationId(UUID reservationId);
}
