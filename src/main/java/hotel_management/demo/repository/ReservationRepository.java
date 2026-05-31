package hotel_management.demo.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hotel_management.demo.constant.ReservationStatus;
import hotel_management.demo.schema.Reservation;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
  List<Reservation> findByStatus(ReservationStatus status);
  List<Reservation> findByGuestId(UUID guestId);
}
