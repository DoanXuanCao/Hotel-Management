package hotel_management.demo.schema;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

import lombok.*;

import hotel_management.demo.constant.RoomStatus;

@Entity
@Table(name = "room")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })

public class Room {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(columnDefinition = "BINARY(16)")
  private UUID id;

  @Column(nullable = false)
  private Integer floor;

  @Column(name = "room_number", nullable = false)
  private String roomNumber;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private RoomStatus status;

  @Column(columnDefinition = "TEXT")
  private String note;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "hotel_id", nullable = false, columnDefinition = "BINARY(16)")
  private Hotel hotel;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "room_type_id", nullable = false, columnDefinition = "BINARY(16)")
  private RoomType roomType;
}