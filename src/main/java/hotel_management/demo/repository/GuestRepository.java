package hotel_management.demo.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hotel_management.demo.schema.Guest;

@Repository
public interface GuestRepository extends JpaRepository<Guest, UUID> {
  public Guest findByAccountId(UUID id);
}
