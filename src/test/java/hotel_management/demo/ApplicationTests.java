package hotel_management.demo;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import hotel_management.demo.constant.*;
import hotel_management.demo.repository.*;
import hotel_management.demo.schema.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@AutoConfigureMockMvc
class ApplicationTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private HotelRepository hotelRepository;
  @Autowired
  private RoomTypeRepository roomTypeRepository;
  @Autowired
  private RoomRepository roomRepository;
  @Autowired
  private GuestRepository guestRepository;
  @Autowired
  private AccountRepository accountRepository;
  @Autowired
  private ReservationRepository reservationRepository;
  @Autowired
  private ReservationRoomRepository reservationRoomRepository;
  @Autowired
  private EmployeeRepository employeeRepository;
  @Autowired
  private PaymentRepository paymentRepository;

  @AfterEach
  void tearDown() {
    paymentRepository.deleteAll();
    reservationRoomRepository.deleteAll();
    reservationRepository.deleteAll();
    roomRepository.deleteAll();
    roomTypeRepository.deleteAll();
    guestRepository.deleteAll();
    employeeRepository.deleteAll();
    accountRepository.deleteAll();
    hotelRepository.deleteAll();
  }

  @Test
  void testHotelCrudLifecycle() throws Exception {
    Hotel newHotel = new Hotel();
    newHotel.setName("The Grand Hyatt");
    newHotel.setAddress("123 Main St");
    newHotel.setPhone("555-1234");
    newHotel.setEmail("grand@hyatt.com");
    newHotel.setRating(4.3);

    String hotelJson = objectMapper.writeValueAsString(newHotel);

    String result = mockMvc.perform(post("/api/hotels")
        .contentType(MediaType.APPLICATION_JSON)
        .content(hotelJson))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

    Hotel createdHotel = objectMapper.readValue(result, Hotel.class);
    assertThat(createdHotel.getId()).isNotNull();
    assertThat(createdHotel.getName()).isEqualTo("The Grand Hyatt");

    // Read the created Hotel by ID
    mockMvc.perform(get("/api/hotels/" + createdHotel.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("The Grand Hyatt"));

    // Update the Hotel
    createdHotel.setName("The Grand Hyatt Redesigned");
    String updatedHotelJson = objectMapper.writeValueAsString(createdHotel);

    mockMvc.perform(put("/api/hotels/" + createdHotel.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(updatedHotelJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("The Grand Hyatt Redesigned"));

    // Delete the Hotel
    mockMvc.perform(delete("/api/hotels/" + createdHotel.getId()))
        .andExpect(status().isNoContent());

    // Verify deletion
    assertThat(hotelRepository.findById(createdHotel.getId())).isEmpty();

    // Verify Read By ID now returns Not Found
    mockMvc.perform(get("/api/hotels/" + createdHotel.getId()))
        .andExpect(status().isNotFound());
    System.out.println("Test finished");
  }

  // GUEST & ACCOUNT TRANSACTIONAL TESTS

  @Test
  void testGuestCreationAndTransactionalDeletion() throws Exception {
    Account account = new Account();
    account.setUsername("jdoe_user");
    account.setPassword("password123");
    account.setEmail("jane.doe@example.com");

    Guest guest = new Guest();
    guest.setAccount(account);
    guest.setFirstName("Jane");
    guest.setLastName("Doe");
    guest.setPhone("999-888-7777");

    // Create Guest (should also create Account due to service logic)
    String guestJson = objectMapper.writeValueAsString(guest);

    String result = mockMvc.perform(post("/api/guests")
        .contentType(MediaType.APPLICATION_JSON)
        .content(guestJson))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

    Guest createdGuest = objectMapper.readValue(result, Guest.class);
    UUID guestId = createdGuest.getId();
    UUID accountId = createdGuest.getAccount().getId();

    assertThat(guestId).isNotNull();
    assertThat(accountId).isNotNull();

    // Verify Account was saved separately
    assertThat(accountRepository.findById(accountId)).isPresent();

    // Transactional Delete Guest
    mockMvc.perform(delete("/api/guests/" + guestId))
        .andExpect(status().isNoContent());

    // Verify both entities are gone
    assertThat(guestRepository.findById(guestId)).isEmpty();
    assertThat(accountRepository.findById(accountId)).isEmpty();
    System.out.println("Test finished");
  }

  @Test
  void testReservationCreationWithRooms() throws Exception {
    // --- Hotel ---
    Hotel hotel = new Hotel();
    hotel.setName("Test Hotel");
    hotel.setAddress("123 Main St");
    hotel.setPhone("555-1234");
    hotel.setEmail("hotel@test.com");
    hotel.setRating((double) 4);
    hotel = hotelRepository.save(hotel);

    // --- RoomType ---
    RoomType roomType = new RoomType();
    roomType.setName("Suite");
    roomType.setDescription("Luxury suite");
    roomType.setCapacity(2);
    roomType.setBasePrice(200);
    roomType = roomTypeRepository.save(roomType);

    // --- Account & Guest ---
    Account account = new Account();
    account.setUsername("guest4");
    account.setEmail("guest4@test.com");
    account.setPassword("pass");
    account.setRole(Role.GUEST);
    account.setIsActive(true);
    account = accountRepository.save(account);

    Guest guest = new Guest();
    guest.setAccount(account);
    guest.setFirstName("John4");
    guest.setLastName("Doe4");
    guest.setPhone("1234567894");
    guest = guestRepository.save(guest);

    // --- Room ---
    Room room = new Room();
    room.setRoomNumber("101");
    room.setFloor(1);
    room.setStatus(RoomStatus.AVAILABLE);
    room.setHotel(hotel);
    room.setRoomType(roomType);
    room = roomRepository.save(room);

    // --- Employee ---
    Account empAccount = new Account();
    empAccount.setUsername("emp3");
    empAccount.setEmail("emp3@test.com");
    empAccount.setPassword("empPass");
    empAccount.setRole(Role.EMPLOYEE);
    empAccount.setIsActive(true);
    empAccount = accountRepository.save(empAccount);

    Employee employee = new Employee();
    employee.setFirstName("Alice");
    employee.setLastName("Smith");
    employee.setHotel(hotel);
    employee.setSalary(1000000);
    employee.setPosition("Manager");
    employee.setHireDate(LocalDate.now().minusYears(2));
    employee.setAccount(empAccount);
    employee = employeeRepository.save(employee);

    // --- Reservation ---
    LocalDateTime now = LocalDateTime.now();
    Reservation reservation = new Reservation();
    reservation.setStayDuration(2);
    reservation.setStatus(ReservationStatus.BOOKED);
    reservation.setCheckin(now.plusDays(1));
    reservation.setCheckout(now.plusDays(3));
    reservation.setCreatedAt(now);
    reservation.setGuest(guest);
    reservation.setEmployee(employee);
    reservation = reservationRepository.save(reservation);

    // --- ReservationRoom link ---
    ReservationRoom rr = new ReservationRoom();
    rr.setReservation(reservation);
    rr.setRoom(room);

    // Create and assign composite ID
    ReservationRoomId rrId = new ReservationRoomId();
    rrId.setReservationId(reservation.getId()); // may be null before persist
    rrId.setRoomId(room.getId());
    rr.setId(rrId);

    // Add to reservation (bidirectional)
    reservation.getReservationRooms().add(rr);

    // Save reservation (cascades to ReservationRoom)
    reservation = reservationRepository.save(reservation);

    // --- Assertions ---
    Reservation fetched = reservationRepository.findById(reservation.getId()).orElseThrow();
    assertThat(fetched.getReservationRooms()).hasSize(1);
    assertThat(fetched.getGuest().getId()).isEqualTo(guest.getId());
  }

  @Test
  void testPaymentCreation() throws Exception {
    // --- Hotel ---
    Hotel hotel = new Hotel();
    hotel.setName("Payment Hotel");
    hotel.setAddress("456 Payment St");
    hotel.setPhone("555-5678");
    hotel.setEmail("payment@test.com");
    hotel.setRating((double) 5);
    hotel = hotelRepository.save(hotel);

    // --- RoomType ---
    RoomType roomType = new RoomType();
    roomType.setName("Deluxe");
    roomType.setDescription("Deluxe Room");
    roomType.setCapacity(2);
    roomType.setBasePrice(150);
    roomType = roomTypeRepository.save(roomType);

    // --- Room ---
    Room room = new Room();
    room.setRoomNumber("201");
    room.setFloor(2);
    room.setStatus(RoomStatus.AVAILABLE);
    room.setHotel(hotel);
    room.setRoomType(roomType);
    room = roomRepository.save(room);

    // --- Account & Guest ---
    Account guestAccount = new Account();
    guestAccount.setUsername("guestPayment");
    guestAccount.setEmail("guestPayment@test.com");
    guestAccount.setPassword("pass");
    guestAccount.setRole(Role.GUEST);
    guestAccount.setIsActive(true);
    guestAccount = accountRepository.save(guestAccount);

    Guest guest = new Guest();
    guest.setAccount(guestAccount);
    guest.setFirstName("Pay");
    guest.setLastName("Test");
    guest.setPhone("9876543210");
    guest = guestRepository.save(guest);

    // --- Employee ---
    Account empAccount = new Account();
    empAccount.setUsername("empPayment");
    empAccount.setEmail("empPayment@test.com");
    empAccount.setPassword("pass");
    empAccount.setRole(Role.EMPLOYEE);
    empAccount.setIsActive(true);
    empAccount = accountRepository.save(empAccount);

    Employee employee = new Employee();
    employee.setFirstName("Alice");
    employee.setLastName("Pay");
    employee.setHotel(hotel);
    employee.setSalary(20000);
    employee.setPosition("Manager");
    employee.setHireDate(LocalDate.now().minusYears(1));
    employee.setAccount(empAccount);
    employee = employeeRepository.save(employee);

    // --- Reservation ---
    LocalDateTime now = LocalDateTime.now();
    Reservation reservation = new Reservation();
    reservation.setStayDuration(3);
    reservation.setStatus(ReservationStatus.BOOKED);
    reservation.setCheckin(now.plusDays(1));
    reservation.setCheckout(now.plusDays(4));
    reservation.setCreatedAt(now);
    reservation.setGuest(guest);
    reservation.setEmployee(employee);
    reservation = reservationRepository.save(reservation);

    // --- Payment ---
    Payment payment = new Payment();
    payment.setReservation(reservation);
    payment.setAmount(45000);
    payment.setMethod(PaymentMethod.CASH);
    payment.setPaymentDate(now.plusDays(1));
    payment = paymentRepository.save(payment);

    // --- Assertions ---
    Payment fetchedPayment = paymentRepository.findById(payment.getId()).orElseThrow();
    assertThat(fetchedPayment.getReservation().getId()).isEqualTo(reservation.getId());
    assertThat(fetchedPayment.getAmount()).isEqualTo(45000);
    assertThat(fetchedPayment.getMethod()).isEqualTo(PaymentMethod.CASH);
  }
}