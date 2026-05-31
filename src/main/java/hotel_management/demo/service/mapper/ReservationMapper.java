package hotel_management.demo.service.mapper;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import hotel_management.demo.dto.ReservationDTO;
import hotel_management.demo.dto.ReservationRoomDTO;
import hotel_management.demo.schema.Reservation;

public class ReservationMapper {
  public ReservationDTO toDTO(Reservation reservation) {
    if (reservation == null)
      return null;

    List<ReservationRoomDTO> roomDTOs = reservation.getReservationRooms() == null ? List.of()
        : reservation.getReservationRooms().stream()
            .filter(rr -> rr != null && rr.getRoom() != null)
            .map(rr -> new ReservationRoomDTO(
                rr.getId().getRoomId(),
                rr.getRoom().getId(),
                rr.getRoom().getRoomNumber(),
                rr.getRoom().getHotel().getId(),
                rr.getRoom().getHotel().getName(),
                rr.getRoom().getStatus() != null ? rr.getRoom().getStatus().name() : null,
                rr.getRoom().getNote(),
                rr.getRoom().getRoomType().getId()))
            .collect(Collectors.toList());

    UUID guestId = reservation.getGuest() != null ? reservation.getGuest().getId() : null;
    String guestName = reservation.getGuest() != null
        ? reservation.getGuest().getFirstName() + " " + reservation.getGuest().getLastName()
        : null;

    UUID employeeId = reservation.getEmployee() != null ? reservation.getEmployee().getId() : null;
    String employeeName = reservation.getEmployee() != null
        ? reservation.getEmployee().getFirstName() + " " + reservation.getEmployee().getLastName()
        : null;

    return new ReservationDTO(
        reservation.getId(),
        guestId,
        guestName,
        reservation.getCheckin() != null ? reservation.getCheckin() : null,
        reservation.getCheckout() != null ? reservation.getCheckout() : null,
        reservation.getStatus() != null ? reservation.getStatus().name() : null,
        roomDTOs,
        employeeId,
        employeeName);
  }
}
