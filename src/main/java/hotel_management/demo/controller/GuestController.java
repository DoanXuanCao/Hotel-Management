package hotel_management.demo.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;

import hotel_management.demo.schema.Guest;
import hotel_management.demo.service.GuestService;

@RestController
@RequestMapping("/api/guests")
public class GuestController {

  private final GuestService guestService;

  public GuestController(GuestService guestService) {
    this.guestService = guestService;
  }

  @PostMapping
  public ResponseEntity<Guest> createGuest(@RequestBody Guest guest) {
    try {
      Guest createdGuest = guestService.createGuest(guest);
      return new ResponseEntity<>(createdGuest, HttpStatus.CREATED);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @GetMapping
  public ResponseEntity<List<Guest>> getAllGuests() {
    List<Guest> guests = guestService.getAllGuests();
    return ResponseEntity.ok(guests);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Guest> getGuestById(@PathVariable UUID id) {
    try {
      Guest guest = guestService.getGuestById(id);
      return ResponseEntity.ok(guest);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<Guest> updateGuest(@PathVariable UUID id, @RequestBody Guest guestDetails) {
    try {
      Guest updatedGuest = guestService.updateGuest(id, guestDetails);
      return ResponseEntity.ok(updatedGuest);
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