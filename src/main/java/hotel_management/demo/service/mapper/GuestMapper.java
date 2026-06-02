package hotel_management.demo.service.mapper;

import hotel_management.demo.dto.GuestDTO;
import hotel_management.demo.schema.Account;
import hotel_management.demo.schema.Guest;

public class GuestMapper {

  public GuestDTO toDTO(Guest guest) {
    if (guest == null) return null;
    Account acc = guest.getAccount();
    return new GuestDTO(
        guest.getId(),
        guest.getFirstName(),
        guest.getLastName(),
        guest.getAddress(),
        guest.getOrigin(),
        guest.getPhone(),
        acc != null ? acc.getId() : null,
        acc != null ? acc.getUsername() : null,
        acc != null ? acc.getEmail() : null,
        acc != null ? acc.getIdNumber() : null,
        acc != null ? acc.getDob() : null,
        acc != null && acc.getRole() != null ? acc.getRole().name() : null,
        acc != null ? acc.getCreatedAt() : null);
  }
}
