package hotel_management.demo.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;

import hotel_management.demo.dto.GuestDTO;
import hotel_management.demo.schema.Guest;
import hotel_management.demo.service.GuestService;
import hotel_management.demo.service.mapper.GuestMapper;

@RestController
@RequestMapping("/api/guests")
public class GuestController {

  private final GuestService guestService;
  private final GuestMapper guestMapper = new GuestMapper();

  public GuestController(GuestService guestService) {
    this.guestService = guestService;
  }

  @PostMapping
  public ResponseEntity<GuestDTO> createGuest(@RequestBody Guest guest) {
    try {
      Guest createdGuest = guestService.createGuest(guest);
      return new ResponseEntity<>(guestMapper.toDTO(createdGuest), HttpStatus.CREATED);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @GetMapping
  public ResponseEntity<List<GuestDTO>> getAllGuests() {
    List<GuestDTO> guests = guestService.getAllGuests().stream()
        .map(guestMapper::toDTO)
        .collect(java.util.stream.Collectors.toList());
    return ResponseEntity.ok(guests);
  }

  @GetMapping("/{id}")
  public ResponseEntity<GuestDTO> getGuestById(@PathVariable UUID id) {
    try {
      Guest guest = guestService.getGuestById(id);
      return ResponseEntity.ok(guestMapper.toDTO(guest));
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<GuestDTO> updateGuest(@PathVariable UUID id, @RequestBody Guest guestDetails) {
    try {
      Guest updatedGuest = guestService.updateGuest(id, guestDetails);
      return ResponseEntity.ok(guestMapper.toDTO(updatedGuest));
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteGuest(@PathVariable UUID id) {
    try {
      guestService.deleteGuest(id);
      return ResponseEntity.noContent().build();
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }
}