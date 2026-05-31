package hotel_management.demo.schema;

import java.time.LocalDateTime;
import java.util.UUID;

import java.util.Set;
import java.util.HashSet;

import jakarta.persistence.*;

import lombok.*;

import hotel_management.demo.constant.ReservationStatus;

@Entity
@Table(name = "reservation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Reservation {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(columnDefinition = "BINARY(16)")
  private UUID id;

  @Column(name = "stay_duration")
  private Integer stayDuration;

  @Enumerated(EnumType.STRING)
  private ReservationStatus status;

  @Column(nullable = false)
  private LocalDateTime checkin;

  @Column(nullable = false)
  private LocalDateTime checkout;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "guest_id", nullable = false, columnDefinition = "BINARY(16)")
  private Guest guest;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "employee_id", nullable = true, columnDefinition = "BINARY(16)")
  private Employee employee;

  @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL)
  private Payment payment;

  @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<ReservationRoom> reservationRooms = new HashSet<>();

  public void addRoom(ReservationRoom rr) {
    reservationRooms.add(rr);
    rr.setReservation(this);
  }
}