package hotel_management.demo.schema;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(name = "guest")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })

public class Guest {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(columnDefinition = "BINARY(16)")
  private UUID id;

  @Column(name = "first_name", nullable = true)
  private String firstName;

  @Column(name = "last_name", nullable = true)
  private String lastName;

  private String address;
  private String origin;
  private String phone;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "account_id", nullable = false, columnDefinition = "BINARY(16)", unique = true)
  private Account account;
}