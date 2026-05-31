package hotel_management.demo.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import hotel_management.demo.schema.ReservationRoom;
import hotel_management.demo.service.ReservationRoomService;

@RestController
@RequestMapping("/api/reservation-rooms")
public class ReservationRoomController {

  private final ReservationRoomService service;

  public ReservationRoomController(ReservationRoomService service) {
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<ReservationRoom> create(@RequestBody ReservationRoom rr) {
    return ResponseEntity.ok(service.create(rr));
  }

  @GetMapping("/{reservationId}/{roomId}")
  public ResponseEntity<ReservationRoom> get(
      @PathVariable UUID reservationId,
      @PathVariable UUID roomId) {

    return ResponseEntity.ok(service.getById(reservationId, roomId));
  }

  @GetMapping
  public ResponseEntity<List<ReservationRoom>> getAll() {
    return ResponseEntity.ok(service.getAll());
  }

  @DeleteMapping("/{reservationId}/{roomId}")
  public ResponseEntity<Void> delete(
      @PathVariable UUID reservationId,
      @PathVariable UUID roomId) {

    service.delete(reservationId, roomId);
    return ResponseEntity.noContent().build();
  }
}
