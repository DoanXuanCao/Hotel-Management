package hotel_management.demo.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import hotel_management.demo.repository.RoomTypeRepository;
import hotel_management.demo.schema.RoomType;

@Service
public class RoomTypeService {
  private final RoomTypeRepository roomTypeRepository;

  public RoomTypeService(RoomTypeRepository roomTypeRepository) {
    this.roomTypeRepository = roomTypeRepository;
  }

  public RoomType createRoomType(RoomType roomType) {
    if (roomType == null) {
      throw new IllegalArgumentException("RoomType cannot be null");
    }
    return roomTypeRepository.save(roomType);
  }

  public RoomType getRoomTypeById(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("RoomType ID cannot be null");
    }
    return roomTypeRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("RoomType not found with ID: " + id));
  }

  public List<RoomType> getAllRoomTypes() {
    return roomTypeRepository.findAll();
  }

  public RoomType updateRoomType(UUID id, RoomType details) {
    RoomType existing = getRoomTypeById(id);
    existing.setName(details.getName());
    existing.setBasePrice(details.getBasePrice());
    existing.setDescription(details.getDescription());
    return roomTypeRepository.save(existing);
  }

  public void deleteRoomType(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("RoomType ID cannot be null");
    }
    if (!roomTypeRepository.existsById(id)) {
      throw new EntityNotFoundException("RoomType not found with ID: " + id);
    }
    roomTypeRepository.deleteById(id);
  }
}