package hotel_management.demo.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hotel_management.demo.schema.Hotel;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, UUID> {
}