package hotel_management.demo.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;
import hotel_management.demo.dto.PaymentDTO;
import hotel_management.demo.schema.Payment;
import hotel_management.demo.service.PaymentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/payments")
public class PaymentController {
  private final PaymentService paymentService;

  public PaymentController(PaymentService paymentService) {
    this.paymentService = paymentService;
  }

  @PostMapping
  public ResponseEntity<Payment> createPayment(@RequestBody Payment payment) {
    try {
      return new ResponseEntity<>(paymentService.createPayment(payment), HttpStatus.CREATED);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().build();
    }
  }


  @GetMapping("/{id}")
  public PaymentDTO getPayment(@PathVariable UUID id) {
    Payment payment = paymentService.getPaymentById(id);
    return paymentService.toDTO(payment);
  }

  @GetMapping
  public List<PaymentDTO> getAllPayments() {
    return paymentService.getAllPayments().stream()
        .map(paymentService::toDTO)
        .collect(Collectors.toList());
  }

  @GetMapping("reservation/{reservationId}")
  public PaymentDTO getPaymentByReservationId(@PathVariable UUID reservationId ) {
    Payment payment = paymentService.getPaymentByReservations(reservationId);
    return paymentService.toDTO(payment);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Payment> updatePayment(@PathVariable UUID id, @RequestBody Payment details) {
    try {
      return ResponseEntity.ok(paymentService.updatePayment(id, details));
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletePayment(@PathVariable UUID id) {
    try {
      paymentService.deletePayment(id);
      return ResponseEntity.noContent().build();
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }
}