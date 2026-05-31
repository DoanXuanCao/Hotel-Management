package hotel_management.demo.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import hotel_management.demo.dto.PaymentDTO;
import hotel_management.demo.dto.ReservationDTO;
import hotel_management.demo.repository.PaymentRepository;
import hotel_management.demo.service.mapper.ReservationMapper;

import hotel_management.demo.schema.Payment;

@Service
public class PaymentService {
  private final PaymentRepository paymentRepository;
  private final ReservationService reservationService;
  private final ReservationMapper reservationMapper = new ReservationMapper();

  public PaymentService(
      PaymentRepository paymentRepository,
      ReservationService reservationService) {
    this.paymentRepository = paymentRepository;
    this.reservationService = reservationService;
  }

  public Payment createPayment(Payment payment) {
    if (payment.getReservation() == null || payment.getReservation().getId() == null) {
      throw new IllegalArgumentException("Payment must be linked to a Reservation.");
    }
    reservationService.getReservationById(payment.getReservation().getId());

    return paymentRepository.save(payment);
  }

  public Payment getPaymentById(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("Payment ID cannot be null");
    }
    return paymentRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Payment not found with ID: " + id));
  }

  public List<Payment> getAllPayments() {
    return paymentRepository.findAll();
  }

  public Payment getPaymentByReservations(UUID reservationId) {
    return paymentRepository.findByReservationId(reservationId);
  }

  public List<Payment> getPaymentsByMethod(String method) {
    final String AVAILABLE_STATUS = method;

    return paymentRepository.findByMethod(AVAILABLE_STATUS);
  }

  public Payment updatePayment(UUID id, Payment details) {
    Payment existing = getPaymentById(id);

    existing.setAmount(details.getAmount());
    existing.setPaymentDate(details.getPaymentDate());
    existing.setMethod(details.getMethod());

    return paymentRepository.save(existing);
  }

  public void deletePayment(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("Payment ID cannot be null");
    }
    if (!paymentRepository.existsById(id)) {
      throw new EntityNotFoundException("Payment not found with ID: " + id);
    }
    paymentRepository.deleteById(id);
  }

  public PaymentDTO toDTO(Payment payment) {
    if (payment == null)
      return null;

    ReservationDTO reservations = payment.getReservation() != null
        ? reservationMapper.toDTO(payment.getReservation())
        : null;
    UUID guestId = payment.getReservation() != null && payment.getReservation().getGuest() != null
        ? payment.getReservation().getGuest().getId()
        : null;
    String guestName = payment.getReservation() != null && payment.getReservation().getGuest() != null
        ? payment.getReservation().getGuest().getFirstName() + " " + payment.getReservation().getGuest().getLastName()
        : null;

    return new PaymentDTO(
        payment.getId(),
        reservations,
        guestId,
        guestName,
        payment.getAmount(),
        payment.getMethod() != null ? payment.getMethod().name() : null,
        payment.getPaymentDate());
  }
}