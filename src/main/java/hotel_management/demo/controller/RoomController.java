package hotel_management.demo.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;
import hotel_management.demo.schema.Room;
import hotel_management.demo.service.RoomService;
import hotel_management.demo.dto.RoomDTO;
import hotel_management.demo.service.mapper.RoomMapper;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
  private final RoomService roomService;
  private final RoomMapper roomMapper;

  public RoomController(RoomService roomService, RoomMapper roomMapper) {
    this.roomService = roomService;
    this.roomMapper = roomMapper;
  }

  @GetMapping("/available")
  public ResponseEntity<List<Room>> getAvailableRooms() {
    List<Room> availableRooms = roomService.getAvailableRooms();
    return ResponseEntity.ok(availableRooms);
  }

  @PostMapping
  public ResponseEntity<RoomDTO> createRoom(@RequestBody Room room) {
    try {
      Room createdRoom = roomService.createRoom(room);
      RoomDTO roomDTO = roomMapper.toDTO(createdRoom);
      return new ResponseEntity<>(roomDTO, HttpStatus.CREATED);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.badRequest().body(null);
    }
  }

  @GetMapping
  public ResponseEntity<List<RoomDTO>> getAllRooms() {
    List<Room> rooms = roomService.getAllRooms();
    List<RoomDTO> roomDTOs = rooms.stream()
        .map(roomMapper::toDTO)
        .collect(Collectors.toList());
    return ResponseEntity.ok(roomDTOs);
  }

  @GetMapping("/{id}")
  public ResponseEntity<RoomDTO> getRoomById(@PathVariable UUID id) {
    try {
      Room room = roomService.getRoomById(id);
      RoomDTO roomDTO = roomMapper.toDTO(room);
      return ResponseEntity.ok(roomDTO);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/hotels/{id}")
  public ResponseEntity<List<RoomDTO>> getRoomByHotelId(@PathVariable UUID id) {
    try {
      List<Room> rooms = roomService.getRoomsByHotelId(id);
      List<RoomDTO> roomDTOs = rooms.stream()
        .map(roomMapper::toDTO)
        .collect(Collectors.toList());
      return ResponseEntity.ok(roomDTOs);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<Room> updateRoom(@PathVariable UUID id, @RequestBody Room details) {
    try {
      return ResponseEntity.ok(roomService.updateRoom(id, details));
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteRoom(@PathVariable UUID id) {
    try {
      roomService.deleteRoom(id);
      return ResponseEntity.noContent().build();
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }
}