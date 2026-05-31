package hotel_management.demo.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;

import hotel_management.demo.schema.RoomType;
import hotel_management.demo.service.RoomTypeService;

@RestController
@RequestMapping("/api/roomtypes")
public class RoomTypeController {
  private final RoomTypeService roomTypeService;

  public RoomTypeController(RoomTypeService roomTypeService) {
    this.roomTypeService = roomTypeService;
  }

  @PostMapping
  public ResponseEntity<RoomType> createRoomType(@RequestBody RoomType roomType) {
    return new ResponseEntity<>(roomTypeService.createRoomType(roomType), HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<RoomType>> getAllRoomTypes() {
    return ResponseEntity.ok(roomTypeService.getAllRoomTypes());
  }

  @GetMapping("/{id}")
  public ResponseEntity<RoomType> getRoomTypeById(@PathVariable UUID id) {
    try {
      return ResponseEntity.ok(roomTypeService.getRoomTypeById(id));
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<RoomType> updateRoomType(@PathVariable UUID id, @RequestBody RoomType details) {
    try {
      return ResponseEntity.ok(roomTypeService.updateRoomType(id, details));
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteRoomType(@PathVariable UUID id) {
    try {
      roomTypeService.deleteRoomType(id);
      return ResponseEntity.noContent().build();
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }
}