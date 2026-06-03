package hotel_management.demo.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import hotel_management.demo.constant.RoomStatus;
import hotel_management.demo.schema.Room;
import jakarta.persistence.LockModeType;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {
  List<Room> findByStatus(RoomStatus status);

  List<Room> getRoomsByHotelId(UUID hoteId);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT r FROM Room r WHERE r.id = :id")
  Optional<Room> findByIdWithLock(@Param("id") UUID id);
}