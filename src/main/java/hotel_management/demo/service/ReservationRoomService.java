package hotel_management.demo.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import hotel_management.demo.schema.ReservationRoom;
import hotel_management.demo.schema.ReservationRoomId;
import hotel_management.demo.repository.ReservationRoomRepository;

@Service
public class ReservationRoomService {

  private final ReservationRoomRepository repository;

  public ReservationRoomService(ReservationRoomRepository repository) {
    this.repository = repository;
  }

  @Transactional
  public ReservationRoom create(ReservationRoom rr) {

    if (rr.getReservation() == null || rr.getRoom() == null) {
      throw new IllegalArgumentException("Reservation and Room must not be null");
    }

    return repository.save(rr);
  }

  public ReservationRoom getById(UUID reservationId, UUID roomId) {
    return repository.findById(
        new ReservationRoomId(reservationId, roomId)
    ).orElseThrow(() -> new EntityNotFoundException(
        "ReservationRoom not found with reservationId=" + reservationId +
        " and roomId=" + roomId
    ));
  }

  public List<ReservationRoom> getAll() {
    return repository.findAll();
  }

  @Transactional
  public void delete(UUID reservationId, UUID roomId) {
    ReservationRoomId id = new ReservationRoomId(reservationId, roomId);
    if (!repository.existsById(id)) {
      throw new EntityNotFoundException(
          "ReservationRoom not found with reservationId=" + reservationId + " and roomId=" + roomId);
    }
    repository.deleteById(id);
  }
}
