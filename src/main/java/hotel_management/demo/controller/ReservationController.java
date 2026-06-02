package hotel_management.demo.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import hotel_management.demo.constant.ReservationStatus;
import hotel_management.demo.dto.ReservationDTO;
import hotel_management.demo.schema.Reservation;
import hotel_management.demo.service.ReservationService;
import hotel_management.demo.service.mapper.ReservationMapper;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

  private final ReservationService reservationService;
  private final ReservationMapper reservationMapper = new ReservationMapper();

  public ReservationController(ReservationService reservationService) {
    this.reservationService = reservationService;

  }

  @PostMapping
  public ResponseEntity<?> createReservation(@RequestBody Reservation reservation) {
    try {
      return ResponseEntity.ok(reservationService.createReservation(reservation));
    } catch (EntityNotFoundException e) {
      return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }
  }

  @GetMapping("/{id}")
  public ReservationDTO getReservation(@PathVariable UUID id) {
    Reservation reservation = reservationService.getReservationById(id);
    return reservationMapper.toDTO(reservation);
  }

  @GetMapping
  public List<ReservationDTO> getAllReservations() {
    return reservationService.getAllReservations().stream()
        .map(reservationMapper::toDTO)
        .collect(Collectors.toList());
  }

  @PutMapping("/{id}")
  public ResponseEntity<Reservation> updateReservation(@PathVariable UUID id, @RequestBody Reservation reservation) {
    return ResponseEntity.ok(reservationService.updateReservation(id, reservation));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteReservation(@PathVariable UUID id) {
    reservationService.deleteReservation(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/pending")
  public List<ReservationDTO> getPendingReservations() {
    return reservationService.getPendingReservations().stream()
        .map(reservationMapper::toDTO)
        .collect(Collectors.toList());
  }

  @GetMapping("/guest/{guestId}")
  public List<ReservationDTO> getReservationsByGuest(@PathVariable UUID guestId) {
    return reservationService.getReservationsByGuestId(guestId).stream()
        .map(reservationMapper::toDTO)
        .collect(Collectors.toList());
  }

  @PutMapping("/{id}/approve")
  public ResponseEntity<ReservationDTO> approveReservation(
      @PathVariable UUID id,
      @RequestBody Map<String, String> body) {
    String empIdStr = body.get("employeeId");
    UUID employeeId = (empIdStr != null && !empIdStr.isBlank()) ? UUID.fromString(empIdStr) : null;
    return ResponseEntity.ok(reservationMapper.toDTO(reservationService.approveReservation(id, employeeId)));
  }

  @PutMapping("/{id}/reject")
  public ResponseEntity<ReservationDTO> rejectReservation(@PathVariable UUID id) {
    return ResponseEntity.ok(reservationMapper.toDTO(reservationService.rejectReservation(id)));
  }

  @PutMapping("/{id}/cancel")
  public ResponseEntity<ReservationDTO> cancelReservation(@PathVariable UUID id) {
    try {
      return ResponseEntity.ok(reservationMapper.toDTO(reservationService.cancelReservation(id)));
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @GetMapping("/status/{status}")
  public List<ReservationDTO> getReservationsByStatus(@PathVariable String status) {
    try {
      ReservationStatus s = ReservationStatus.valueOf(status.toUpperCase());
      return reservationService.getReservationsByStatus(s).stream()
          .map(reservationMapper::toDTO)
          .collect(Collectors.toList());
    } catch (IllegalArgumentException e) {
      return java.util.Collections.emptyList();
    }
  }
}
