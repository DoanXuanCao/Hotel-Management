package hotel_management.demo.schema;

import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(name = "hotel")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })

public class Hotel {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(columnDefinition = "BINARY(16)")
  private UUID id;

  @Column(nullable = false)
  private String name;

  private Double rating;
  private String phone;
  private String email;
  private String address;

  @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL)
  private Set<Room> rooms;

  @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL)
  private Set<Employee> employees;
}