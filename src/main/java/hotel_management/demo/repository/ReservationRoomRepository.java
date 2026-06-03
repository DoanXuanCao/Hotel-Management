package hotel_management.demo.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import hotel_management.demo.constant.ReservationStatus;
import hotel_management.demo.schema.ReservationRoom;
import hotel_management.demo.schema.ReservationRoomId;

@Repository
public interface ReservationRoomRepository extends JpaRepository<ReservationRoom, ReservationRoomId> {

  @Modifying
  @Query("DELETE FROM ReservationRoom rr WHERE rr.reservation.id = :reservationId")
  void deleteByReservationId(UUID reservationId);

  @Query("SELECT COUNT(rr) FROM ReservationRoom rr " +
         "WHERE rr.room.id = :roomId " +
         "AND rr.reservation.status NOT IN :excludedStatuses " +
         "AND rr.reservation.checkin < :checkout " +
         "AND rr.reservation.checkout > :checkin")
  long countOverlappingReservation(
      @Param("roomId") UUID roomId,
      @Param("checkin") LocalDateTime checkin,
      @Param("checkout") LocalDateTime checkout,
      @Param("excludedStatuses") List<ReservationStatus> excludedStatuses);

  @Query("SELECT COUNT(rr) FROM ReservationRoom rr " +
         "WHERE rr.room.id = :roomId " +
         "AND rr.reservation.id != :excludeReservationId " +
         "AND rr.reservation.status = :checkedInStatus")
  long countOtherCheckedIn(
      @Param("roomId") UUID roomId,
      @Param("excludeReservationId") UUID excludeReservationId,
      @Param("checkedInStatus") ReservationStatus checkedInStatus);
}
