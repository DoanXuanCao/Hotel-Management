package hotel_management.demo.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import hotel_management.demo.constant.RoomStatus;
import hotel_management.demo.repository.RoomRepository;
import hotel_management.demo.schema.Room;

@Service
public class RoomService {
  private final RoomRepository roomRepository;
  private final HotelService hotelService;
  private final RoomTypeService roomTypeService;

  public RoomService(RoomRepository roomRepository, HotelService hotelService, RoomTypeService roomTypeService) {
    this.roomRepository = roomRepository;
    this.hotelService = hotelService;
    this.roomTypeService = roomTypeService;
  }

  public Room createRoom(Room room) {
    hotelService.getHotelById(room.getHotel().getId());
    roomTypeService.getRoomTypeById(room.getRoomType().getId());
    return roomRepository.save(room);
  }

  public Room getRoomById(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("Room ID cannot be null");
    }
    return roomRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Room not found with ID: " + id));
  }

  public Room getRoomByIdWithLock(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("Room ID cannot be null");
    }
    return roomRepository.findByIdWithLock(id)
        .orElseThrow(() -> new EntityNotFoundException("Room not found with ID: " + id));
  }

  public List<Room> getAllRooms() {
    return roomRepository.findAll();
  }

  public List<Room> getAvailableRooms() {
    return roomRepository.findByStatus(RoomStatus.AVAILABLE);
  }

  public List<Room> getOccupiedRooms() {
    return roomRepository.findByStatus(RoomStatus.OCCUPIED);
  }

  public Room updateRoom(UUID id, Room details) {
    Room existing = getRoomById(id);
    existing.setRoomNumber(details.getRoomNumber());
    existing.setStatus(details.getStatus());

    return roomRepository.save(existing);
  }

  public void deleteRoom(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("Room ID cannot be null");
    }
    if (!roomRepository.existsById(id)) {
      throw new EntityNotFoundException("Room not found with ID: " + id);
    }
    roomRepository.deleteById(id);
  }

  public void updateRoomStatus(UUID id, RoomStatus status) {
    Room room = getRoomById(id);
    room.setStatus(status);
    roomRepository.save(room);
  }

  public List<Room> getRoomsByHotelId(UUID hotelId) {
    return roomRepository.getRoomsByHotelId(hotelId);
  }
}