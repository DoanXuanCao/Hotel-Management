package hotel_management.demo.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hotel_management.demo.constant.RoomStatus;
import hotel_management.demo.schema.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {
  List<Room> findByStatus(RoomStatus status);

  List<Room> getRoomsByHotelId(UUID hoteId);
}