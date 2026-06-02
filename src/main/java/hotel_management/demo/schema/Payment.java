package hotel_management.demo.schema;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

import lombok.*;

import hotel_management.demo.constant.PaymentMethod;;

@Entity
@Table(name = "payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })

public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(columnDefinition = "BINARY(16)")
  private UUID id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PaymentMethod method;

  @Column(nullable = false, precision = 10, scale = 2)
  private Integer amount;

  @JsonProperty("payment_date")
  @Column(name = "payment_date", nullable = true)
  private LocalDateTime paymentDate;

  @OneToOne
  @JoinColumn(name = "reservation_id", referencedColumnName = "id", columnDefinition = "BINARY(16)", nullable = false, unique = true)
  private Reservation reservation;
}