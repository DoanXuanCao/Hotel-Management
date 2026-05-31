package hotel_management.demo.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hotel_management.demo.schema.RoomType;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, UUID> {
}