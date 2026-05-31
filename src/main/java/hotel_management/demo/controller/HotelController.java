package hotel_management.demo.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;
import hotel_management.demo.dto.HotelDTO;
import hotel_management.demo.schema.Hotel;
import hotel_management.demo.service.HotelService;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {

  private final HotelService hotelService;

  public HotelController(HotelService hotelService) {
    this.hotelService = hotelService;
  }

  @PostMapping
  public ResponseEntity<Hotel> createHotel(@RequestBody Hotel hotel) {
    try {
      Hotel createdHotel = hotelService.createHotel(hotel);
      return new ResponseEntity<>(createdHotel, HttpStatus.CREATED);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<HotelDTO> getHotel(@PathVariable UUID id) {
    Hotel hotel = hotelService.getHotelById(id);
    HotelDTO hotelDTO = hotelService.toDTO(hotel);
    return ResponseEntity.ok(hotelDTO);
  }

  @GetMapping
  public ResponseEntity<List<HotelDTO>> getAllHotels() {
    List<HotelDTO> hotelDTOs = hotelService.getAllHotels().stream()
        .map(hotelService::toDTO)
        .collect(Collectors.toList());
    return ResponseEntity.ok(hotelDTOs);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Hotel> updateHotel(@PathVariable UUID id, @RequestBody Hotel hotelDetails) {
    try {
      Hotel updatedHotel = hotelService.updateHotel(id, hotelDetails);
      return ResponseEntity.ok(updatedHotel);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteHotel(@PathVariable UUID id) {
    try {
      hotelService.deleteHotel(id);
      return ResponseEntity.noContent().build();
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }
}