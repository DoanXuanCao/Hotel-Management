package hotel_management.demo.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import hotel_management.demo.constant.ReservationStatus;
import hotel_management.demo.constant.RoomStatus;
import hotel_management.demo.repository.ReservationRepository;
import hotel_management.demo.repository.ReservationRoomRepository;
import hotel_management.demo.schema.Reservation;
import hotel_management.demo.schema.ReservationRoom;
import hotel_management.demo.schema.Room;
import hotel_management.demo.schema.Employee;
import hotel_management.demo.schema.Guest;
import hotel_management.demo.schema.Payment;
import hotel_management.demo.constant.PaymentMethod;

@Service
public class ReservationService {
  private final ReservationRepository reservationRepository;
  private final GuestService guestService;
  private final RoomService roomService;
  private final EmployeeService employeeService;
  private final ReservationRoomRepository reservationRoomRepository;

  public ReservationService(ReservationRepository reservationRepository,
      ReservationRoomRepository reservationRoomRepository, GuestService guestService, RoomService roomService,
      EmployeeService employeeService) {
    this.reservationRepository = reservationRepository;
    this.guestService = guestService;
    this.roomService = roomService;
    this.employeeService = employeeService;
    this.reservationRoomRepository = reservationRoomRepository;
  }

  @Transactional
  public Reservation createReservation(Reservation req) {
    Reservation reservation = new Reservation();
    reservation.setCreatedAt(LocalDateTime.now());
    reservation.setCheckin(req.getCheckin());
    reservation.setCheckout(req.getCheckout());
    reservation.setStatus(req.getStatus());

    UUID guestId = req.getGuest() == null ? null : req.getGuest().getId();
    if (guestId == null) {
      throw new RuntimeException("Guest ID is required");
    }

    Guest guest;
    try {
      guest = guestService.getGuestById(guestId);
    } catch (EntityNotFoundException e) {
      guest = guestService.getGuestByAccountId(guestId);
      if (guest == null) {
        throw new EntityNotFoundException("Guest not found with ID or account ID: " + guestId);
      }
    }
    reservation.setGuest(guest);

    if (req.getEmployee() != null && req.getEmployee().getId() != null) {
      Employee employee = employeeService.getEmployeeById(
          req.getEmployee().getId());
      reservation.setEmployee(employee);
    }

    if (req.getReservationRooms() == null || req.getReservationRooms().isEmpty()) {
      throw new RuntimeException("At least one room is required");
    }

    for (ReservationRoom inputRR : req.getReservationRooms()) {
      Room room = roomService.getRoomById(
          inputRR.getRoom().getId());

      ReservationRoom rr = new ReservationRoom();
      rr.setReservation(reservation);
      rr.setRoom(room);

      reservation.getReservationRooms().add(rr);
    }

    long days = ChronoUnit.DAYS.between(
        reservation.getCheckin().toLocalDate(),
        reservation.getCheckout().toLocalDate());
    if (days <= 0)
      days = 1;
    reservation.setStayDuration((int) days);

    Payment payment = new Payment();
    payment.setReservation(reservation);
    payment.setMethod(PaymentMethod.CASH);

    double dailyTotal = reservation.getReservationRooms().stream()
        .mapToDouble(rr -> rr.getRoom().getRoomType().getBasePrice())
        .sum();

    payment.setAmount((int) (dailyTotal * days));
    reservation.setPayment(payment);

    return reservationRepository.save(reservation);
  }

  @Transactional
  public Reservation getReservationById(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("Reservation ID cannot be null");
    }
    return reservationRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Reservation not found with ID: " + id));
  }

  @Transactional
  public List<Reservation> getAllReservations() {
    return reservationRepository.findAll();
  }

  @Transactional
  public Reservation updateReservation(UUID id, Reservation details) {
    if (id == null) return null;
    Reservation reservation = reservationRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Reservation not found"));

    ReservationStatus oldStatus = reservation.getStatus();

    reservation.setCheckin(details.getCheckin());
    reservation.setCheckout(details.getCheckout());
    reservation.setStatus(details.getStatus());

    if (details.getGuest() != null && details.getGuest().getId() != null) {
      Guest guest = guestService.getGuestById(details.getGuest().getId());
      reservation.setGuest(guest);
    }

    if (details.getEmployee() != null && details.getEmployee().getId() != null) {
      Employee employee = employeeService.getEmployeeById(details.getEmployee().getId());
      reservation.setEmployee(employee);
    }

    if (details.getReservationRooms() != null && !details.getReservationRooms().isEmpty()) {
      UUID newRoomId = details.getReservationRooms().iterator().next().getRoom().getId();
      Room newRoom = roomService.getRoomById(newRoomId);

      reservation.getReservationRooms().clear();
      reservationRoomRepository.deleteByReservationId(id);

      ReservationRoom newRR = new ReservationRoom();
      newRR.setReservation(reservation);
      newRR.setRoom(newRoom);
      reservation.getReservationRooms().add(newRR);
    }

    Reservation saved = reservationRepository.save(reservation);

    // Update room statuses based on reservation status transition
    applyRoomStatusForReservation(saved, details.getStatus(), oldStatus);

    return saved;
  }

  private void applyRoomStatusForReservation(Reservation reservation, ReservationStatus newStatus, ReservationStatus oldStatus) {
    if (newStatus == null || newStatus == oldStatus) return;

    RoomStatus targetRoomStatus = null;
    if (newStatus == ReservationStatus.CHECKED_IN) {
      targetRoomStatus = RoomStatus.OCCUPIED;
    } else if (newStatus == ReservationStatus.COMPLETED
        || newStatus == ReservationStatus.CANCELED
        || newStatus == ReservationStatus.NO_SHOW) {
      targetRoomStatus = RoomStatus.AVAILABLE;
    }

    if (targetRoomStatus != null) {
      final RoomStatus finalStatus = targetRoomStatus;
      reservation.getReservationRooms()
          .forEach(rr -> roomService.updateRoomStatus(rr.getRoom().getId(), finalStatus));
    }
  }



  @Transactional
  public void deleteReservation(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("Reservation ID cannot be null");
    }
    if (!reservationRepository.existsById(id)) {
      throw new EntityNotFoundException("Reservation not found with ID: " + id);
    }
    reservationRepository.deleteById(id);
  }

  @Transactional
  public List<Reservation> getPendingReservations() {
    return reservationRepository.findByStatus(ReservationStatus.PENDING);
  }

  @Transactional
  public List<Reservation> getReservationsByGuestId(UUID guestId) {
    return reservationRepository.findByGuestId(guestId);
  }

  @Transactional
  public Reservation approveReservation(UUID id, UUID employeeId) {
    Reservation reservation = reservationRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Reservation not found with ID: " + id));
    reservation.setStatus(ReservationStatus.BOOKED);
    if (employeeId != null) {
      Employee employee = employeeService.getEmployeeById(employeeId);
      reservation.setEmployee(employee);
    }
    return reservationRepository.save(reservation);
  }

  @Transactional
  public Reservation rejectReservation(UUID id) {
    Reservation reservation = reservationRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Reservation not found with ID: " + id));
    ReservationStatus old = reservation.getStatus();
    reservation.setStatus(ReservationStatus.CANCELED);
    Reservation saved = reservationRepository.save(reservation);
    applyRoomStatusForReservation(saved, ReservationStatus.CANCELED, old);
    return saved;
  }

  @Transactional
  public Reservation cancelReservation(UUID id) {
    Reservation reservation = reservationRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Reservation not found with ID: " + id));
    if (reservation.getStatus() != ReservationStatus.PENDING
        && reservation.getStatus() != ReservationStatus.BOOKED) {
      throw new RuntimeException("Only PENDING or BOOKED reservations can be cancelled by the guest.");
    }
    ReservationStatus old = reservation.getStatus();
    reservation.setStatus(ReservationStatus.CANCELED);
    Reservation saved = reservationRepository.save(reservation);
    applyRoomStatusForReservation(saved, ReservationStatus.CANCELED, old);
    return saved;
  }

  @Transactional
  public List<Reservation> getReservationsByStatus(ReservationStatus status) {
    return reservationRepository.findByStatus(status);
  }
}