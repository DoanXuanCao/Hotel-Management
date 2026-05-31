package hotel_management.demo.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hotel_management.demo.schema.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
  List<Payment> findByMethod(String method);

  Payment findByReservationId(UUID reservationId);
}
