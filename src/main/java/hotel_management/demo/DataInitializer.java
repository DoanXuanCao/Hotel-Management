package hotel_management.demo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import hotel_management.demo.constant.PaymentMethod;
import hotel_management.demo.constant.ReservationStatus;
import hotel_management.demo.constant.Role;
import hotel_management.demo.constant.RoomStatus;
import hotel_management.demo.repository.AccountRepository;
import hotel_management.demo.repository.EmployeeRepository;
import hotel_management.demo.repository.GuestRepository;
import hotel_management.demo.repository.HotelRepository;
import hotel_management.demo.repository.PaymentRepository;
import hotel_management.demo.repository.ReservationRepository;
import hotel_management.demo.repository.ReservationRoomRepository;
import hotel_management.demo.repository.RoomRepository;
import hotel_management.demo.repository.RoomTypeRepository;
import hotel_management.demo.schema.Account;
import hotel_management.demo.schema.Employee;
import hotel_management.demo.schema.Guest;
import hotel_management.demo.schema.Hotel;
import hotel_management.demo.schema.Payment;
import hotel_management.demo.schema.Reservation;
import hotel_management.demo.schema.ReservationRoom;
import hotel_management.demo.schema.ReservationRoomId;
import hotel_management.demo.schema.Room;
import hotel_management.demo.schema.RoomType;

@Component
@Order(1)
public class DataInitializer implements CommandLineRunner {

  private final HotelRepository hotelRepository;
  private final RoomTypeRepository roomTypeRepository;
  private final RoomRepository roomRepository;
  private final AccountRepository accountRepository;
  private final EmployeeRepository employeeRepository;
  private final GuestRepository guestRepository;
  private final ReservationRepository reservationRepository;
  private final ReservationRoomRepository reservationRoomRepository;
  private final PaymentRepository paymentRepository;
  private final PasswordEncoder passwordEncoder;

  public DataInitializer(
      HotelRepository hotelRepository,
      RoomTypeRepository roomTypeRepository,
      RoomRepository roomRepository,
      AccountRepository accountRepository,
      EmployeeRepository employeeRepository,
      GuestRepository guestRepository,
      ReservationRepository reservationRepository,
      ReservationRoomRepository reservationRoomRepository,
      PaymentRepository paymentRepository,
      PasswordEncoder passwordEncoder) {
    this.hotelRepository = hotelRepository;
    this.roomTypeRepository = roomTypeRepository;
    this.roomRepository = roomRepository;
    this.accountRepository = accountRepository;
    this.employeeRepository = employeeRepository;
    this.guestRepository = guestRepository;
    this.reservationRepository = reservationRepository;
    this.reservationRoomRepository = reservationRoomRepository;
    this.paymentRepository = paymentRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  @Transactional
  public void run(String... args) {
    if (hotelRepository.count() > 0) {
      System.out.println("[DataInitializer] Database already seeded. Skipping.");
      return;
    }

    System.out.println("[DataInitializer] Seeding initial data...");

    // ── 4 Room Types ────────────────────────────────────────────────────
    RoomType standard = roomTypeRepository.save(new RoomType(null,
        "Standard", "Phòng tiêu chuẩn với tiện nghi cơ bản, giường đôi thoải mái, điều hòa, TV 32 inch.", 2, 500_000));
    RoomType deluxe = roomTypeRepository.save(new RoomType(null,
        "Deluxe", "Phòng cao cấp view thành phố, mini-bar, bồn tắm jacuzzi, TV 43 inch.", 2, 1_000_000));
    RoomType suite = roomTypeRepository.save(new RoomType(null,
        "Suite", "Phòng hạng sang với phòng khách riêng, ban công view toàn cảnh, butler service.", 4, 2_500_000));
    RoomType family = roomTypeRepository.save(new RoomType(null,
        "Family", "Phòng gia đình rộng rãi 60m², 2 phòng ngủ, bếp nhỏ, 2 phòng tắm.", 4, 1_800_000));

    // ── 3 Hotels ─────────────────────────────────────────────────────────
    Hotel hotel1 = hotelRepository.save(new Hotel(null,
        "Grand Palace Hotel", 4.8, "024-3825-1234", "grandpalace@homs.vn",
        "12 Tràng Tiền, Hoàn Kiếm, Hà Nội", null, null));
    Hotel hotel2 = hotelRepository.save(new Hotel(null,
        "Saigon Riverside Hotel", 4.5, "028-3829-2000", "saigon@homs.vn",
        "18 Bến Chương Dương, Quận 1, TP.HCM", null, null));
    Hotel hotel3 = hotelRepository.save(new Hotel(null,
        "Da Nang Beach Resort", 4.7, "0236-395-8888", "danang@homs.vn",
        "10 Trường Sa, Ngũ Hành Sơn, Đà Nẵng", null, null));

    // ── Rooms — Hotel 1: Grand Palace (Hà Nội) ──────────────────────────
    Room gp101 = roomRepository.save(new Room(null, 1, "101", RoomStatus.AVAILABLE, "Phòng hướng đông, nhìn ra sân vườn", hotel1, standard));
    Room gp102 = roomRepository.save(new Room(null, 1, "102", RoomStatus.AVAILABLE, "Phòng hướng tây, yên tĩnh", hotel1, standard));
    Room gp103 = roomRepository.save(new Room(null, 1, "103", RoomStatus.AVAILABLE, null, hotel1, standard));
    Room gp201 = roomRepository.save(new Room(null, 2, "201", RoomStatus.AVAILABLE, "View đường Tràng Tiền", hotel1, deluxe));
    Room gp202 = roomRepository.save(new Room(null, 2, "202", RoomStatus.OCCUPIED,  "View Hồ Gươm – hiện đang có khách", hotel1, deluxe));
    Room gp301 = roomRepository.save(new Room(null, 3, "301", RoomStatus.AVAILABLE, "Tầng thượng, view 360° Hà Nội", hotel1, suite));
    Room gp401 = roomRepository.save(new Room(null, 4, "401", RoomStatus.AVAILABLE, "Phòng Presidential Suite", hotel1, suite));

    // ── Rooms — Hotel 2: Saigon Riverside (TP.HCM) ──────────────────────
    Room sr101 = roomRepository.save(new Room(null, 1, "101", RoomStatus.AVAILABLE, null, hotel2, standard));
    Room sr102 = roomRepository.save(new Room(null, 1, "102", RoomStatus.AVAILABLE, null, hotel2, standard));
    Room sr201 = roomRepository.save(new Room(null, 2, "201", RoomStatus.AVAILABLE, "View sông Sài Gòn", hotel2, deluxe));
    Room sr202 = roomRepository.save(new Room(null, 2, "202", RoomStatus.OCCUPIED,  "Hiện đang có khách check-in", hotel2, deluxe));
    Room sr301 = roomRepository.save(new Room(null, 3, "301", RoomStatus.AVAILABLE, "Suite góc, 2 mặt view sông", hotel2, suite));
    Room sr401 = roomRepository.save(new Room(null, 4, "401", RoomStatus.AVAILABLE, "Penthouse view TP.HCM ban đêm", hotel2, family));

    // ── Rooms — Hotel 3: Da Nang Beach Resort ───────────────────────────
    Room dn101 = roomRepository.save(new Room(null, 1, "101", RoomStatus.AVAILABLE, "Phòng sát biển, nghe tiếng sóng", hotel3, standard));
    Room dn102 = roomRepository.save(new Room(null, 1, "102", RoomStatus.AVAILABLE, null, hotel3, standard));
    Room dn201 = roomRepository.save(new Room(null, 2, "201", RoomStatus.AVAILABLE, "Ban công nhìn thẳng ra biển Mỹ Khê", hotel3, family));
    Room dn202 = roomRepository.save(new Room(null, 2, "202", RoomStatus.AVAILABLE, "Phòng gia đình rộng rãi, 2 giường king", hotel3, family));
    Room dn301 = roomRepository.save(new Room(null, 3, "301", RoomStatus.AVAILABLE, "Beach Suite, hồ bơi riêng", hotel3, suite));

    // ── Admin Account ────────────────────────────────────────────────────
    Account adminAcc = new Account();
    adminAcc.setUsername("admin");
    adminAcc.setEmail("admin@homs.vn");
    adminAcc.setPassword(passwordEncoder.encode("admin123"));
    adminAcc.setRole(Role.ADMIN);
    adminAcc.setIdNumber("001234567890");
    accountRepository.save(adminAcc);

    // ── 3 Employee Accounts & Employees (one per hotel) ──────────────────
    Account empAcc1 = makeAccount("emp01", "emp01@homs.vn", "pass123", Role.EMPLOYEE, "031111111111");
    empAcc1 = accountRepository.save(empAcc1);
    Account empAcc2 = makeAccount("emp02", "emp02@homs.vn", "pass123", Role.EMPLOYEE, "031222222222");
    empAcc2 = accountRepository.save(empAcc2);
    Account empAcc3 = makeAccount("emp03", "emp03@homs.vn", "pass123", Role.EMPLOYEE, "031333333333");
    empAcc3 = accountRepository.save(empAcc3);

    Employee emp1 = employeeRepository.save(new Employee(null,
        "Nguyễn Văn", "An", "0901-234-567", "Receptionist",
        LocalDate.of(2022, 3, 15), 8_000_000, hotel1, empAcc1));
    Employee emp2 = employeeRepository.save(new Employee(null,
        "Trần Thị", "Mai", "0902-345-678", "Front Desk Manager",
        LocalDate.of(2021, 6, 1), 12_000_000, hotel2, empAcc2));
    Employee emp3 = employeeRepository.save(new Employee(null,
        "Lê Quốc", "Bình", "0903-456-789", "Resort Manager",
        LocalDate.of(2020, 9, 10), 15_000_000, hotel3, empAcc3));

    // ── 5 Guest Accounts & Guests ────────────────────────────────────────
    Account ga1 = makeAccount("guest01", "lehoangdung@gmail.com", "pass123", Role.GUEST, "012345678901");
    ga1 = accountRepository.save(ga1);
    Account ga2 = makeAccount("guest02", "phamthilan@gmail.com", "pass123", Role.GUEST, "012345678902");
    ga2 = accountRepository.save(ga2);
    Account ga3 = makeAccount("guest03", "vominhkhoa@gmail.com", "pass123", Role.GUEST, "012345678903");
    ga3 = accountRepository.save(ga3);
    Account ga4 = makeAccount("guest04", "nguyenthihoa@gmail.com", "pass123", Role.GUEST, "012345678904");
    ga4 = accountRepository.save(ga4);
    Account ga5 = makeAccount("guest05", "tranvanlong@gmail.com", "pass123", Role.GUEST, "012345678905");
    ga5 = accountRepository.save(ga5);

    Guest g1 = guestRepository.save(new Guest(null, "Lê Hoàng", "Dũng",
        "12 Lê Lợi, Hoàn Kiếm, Hà Nội", "Hà Nội", "0911-111-111", ga1));
    Guest g2 = guestRepository.save(new Guest(null, "Phạm Thị", "Lan",
        "45 Nguyễn Huệ, Quận 1, TP.HCM", "TP.HCM", "0922-222-222", ga2));
    Guest g3 = guestRepository.save(new Guest(null, "Võ Minh", "Khoa",
        "78 Bạch Đằng, Hải Châu, Đà Nẵng", "Đà Nẵng", "0933-333-333", ga3));
    Guest g4 = guestRepository.save(new Guest(null, "Nguyễn Thị", "Hoa",
        "99 Đinh Tiên Hoàng, Bình Thạnh, TP.HCM", "TP.HCM", "0944-444-444", ga4));
    Guest g5 = guestRepository.save(new Guest(null, "Trần Văn", "Long",
        "25 Phan Chu Trinh, Ngũ Hành Sơn, Đà Nẵng", "Đà Nẵng", "0955-555-555", ga5));

    // ── Reservations & Payments ──────────────────────────────────────────
    // --- COMPLETED (đã thanh toán đầy đủ) ---
    seed(g1, emp1, gp201,
        LocalDateTime.of(2025, 3, 10, 14, 0), LocalDateTime.of(2025, 3, 13, 12, 0),
        ReservationStatus.COMPLETED, PaymentMethod.BANKING,
        LocalDateTime.of(2025, 3, 10, 15, 0));

    seed(g2, emp1, gp301,
        LocalDateTime.of(2025, 4, 5, 14, 0), LocalDateTime.of(2025, 4, 8, 12, 0),
        ReservationStatus.COMPLETED, PaymentMethod.CREDIT_CARD,
        LocalDateTime.of(2025, 4, 5, 16, 0));

    seed(g3, emp2, sr201,
        LocalDateTime.of(2025, 4, 20, 14, 0), LocalDateTime.of(2025, 4, 22, 12, 0),
        ReservationStatus.COMPLETED, PaymentMethod.CASH,
        LocalDateTime.of(2025, 4, 20, 14, 30));

    seed(g4, emp3, dn301,
        LocalDateTime.of(2025, 5, 1, 14, 0), LocalDateTime.of(2025, 5, 5, 12, 0),
        ReservationStatus.COMPLETED, PaymentMethod.BANKING,
        LocalDateTime.of(2025, 5, 1, 14, 45));

    seed(g5, emp3, dn201,
        LocalDateTime.of(2025, 5, 15, 14, 0), LocalDateTime.of(2025, 5, 18, 12, 0),
        ReservationStatus.COMPLETED, PaymentMethod.DEBIT_CARD,
        LocalDateTime.of(2025, 5, 15, 15, 0));

    // --- CANCELED (đã bị hủy) ---
    seed(g1, emp2, sr101,
        LocalDateTime.of(2025, 5, 20, 14, 0), LocalDateTime.of(2025, 5, 22, 12, 0),
        ReservationStatus.CANCELED, PaymentMethod.CASH, null);

    seed(g3, emp1, gp103,
        LocalDateTime.of(2025, 6, 1, 14, 0), LocalDateTime.of(2025, 6, 3, 12, 0),
        ReservationStatus.CANCELED, PaymentMethod.BANKING, null);

    // --- BOOKED (đã xác nhận, chưa check-in) ---
    seed(g2, emp2, sr301,
        LocalDateTime.of(2025, 7, 20, 14, 0), LocalDateTime.of(2025, 7, 24, 12, 0),
        ReservationStatus.BOOKED, PaymentMethod.BANKING, null);

    seed(g4, emp1, gp401,
        LocalDateTime.of(2025, 7, 25, 14, 0), LocalDateTime.of(2025, 7, 27, 12, 0),
        ReservationStatus.BOOKED, PaymentMethod.CREDIT_CARD, null);

    // --- CHECKED_IN (đang ở – phòng OCCUPIED) ---
    seed(g5, emp2, sr202,
        LocalDateTime.of(2025, 7, 15, 14, 0), LocalDateTime.of(2025, 7, 18, 12, 0),
        ReservationStatus.CHECKED_IN, PaymentMethod.CASH, null);

    seed(g3, emp3, gp202,
        LocalDateTime.of(2025, 7, 14, 14, 0), LocalDateTime.of(2025, 7, 16, 12, 0),
        ReservationStatus.CHECKED_IN, PaymentMethod.DEBIT_CARD, null);

    // --- PENDING (chờ nhân viên duyệt – không có employee) ---
    seedPending(g1, dn101,
        LocalDateTime.of(2025, 8, 5, 14, 0), LocalDateTime.of(2025, 8, 8, 12, 0),
        PaymentMethod.BANKING);

    seedPending(g2, sr102,
        LocalDateTime.of(2025, 8, 10, 14, 0), LocalDateTime.of(2025, 8, 12, 12, 0),
        PaymentMethod.CASH);

    seedPending(g4, dn202,
        LocalDateTime.of(2025, 8, 15, 14, 0), LocalDateTime.of(2025, 8, 20, 12, 0),
        PaymentMethod.CREDIT_CARD);

    System.out.println("[DataInitializer] Seeding completed.");
    System.out.println("  ADMIN    → admin     / admin123");
    System.out.println("  EMPLOYEE → emp01     / pass123  (Grand Palace, Hà Nội)");
    System.out.println("  EMPLOYEE → emp02     / pass123  (Saigon Riverside, TP.HCM)");
    System.out.println("  EMPLOYEE → emp03     / pass123  (Da Nang Beach Resort)");
    System.out.println("  GUEST    → guest01   / pass123  (Lê Hoàng Dũng)");
    System.out.println("  GUEST    → guest02   / pass123  (Phạm Thị Lan)");
    System.out.println("  GUEST    → guest03   / pass123  (Võ Minh Khoa)");
    System.out.println("  GUEST    → guest04   / pass123  (Nguyễn Thị Hoa)");
    System.out.println("  GUEST    → guest05   / pass123  (Trần Văn Long)");
    System.out.println("  Data summary: 3 hotels · 19 rooms · 14 reservations · 3 PENDING");
  }

  // ── Helpers ─────────────────────────────────────────────────────────────

  private Account makeAccount(String username, String email, String password,
      Role role, String idNumber) {
    Account a = new Account();
    a.setUsername(username);
    a.setEmail(email);
    a.setPassword(passwordEncoder.encode(password));
    a.setRole(role);
    a.setIdNumber(idNumber);
    return a;
  }

  private void seed(Guest guest, Employee employee, Room room,
      LocalDateTime checkin, LocalDateTime checkout,
      ReservationStatus status, PaymentMethod method,
      LocalDateTime paymentDate) {

    long days = ChronoUnit.DAYS.between(checkin.toLocalDate(), checkout.toLocalDate());
    if (days <= 0) days = 1;

    Reservation r = new Reservation();
    r.setGuest(guest);
    r.setEmployee(employee);
    r.setCheckin(checkin);
    r.setCheckout(checkout);
    r.setStatus(status);
    r.setStayDuration((int) days);
    r.setCreatedAt(checkin.minusDays(2));
    r = reservationRepository.save(r);

    reservationRoomRepository.save(
        new ReservationRoom(new ReservationRoomId(r.getId(), room.getId()), room, r));

    Payment p = new Payment();
    p.setReservation(r);
    p.setMethod(method);
    p.setAmount(room.getRoomType().getBasePrice() * (int) days);
    p.setPaymentDate(paymentDate);
    paymentRepository.save(p);
  }

  private void seedPending(Guest guest, Room room,
      LocalDateTime checkin, LocalDateTime checkout,
      PaymentMethod method) {

    long days = ChronoUnit.DAYS.between(checkin.toLocalDate(), checkout.toLocalDate());
    if (days <= 0) days = 1;

    Reservation r = new Reservation();
    r.setGuest(guest);
    r.setEmployee(null);                    // no employee — awaiting approval
    r.setCheckin(checkin);
    r.setCheckout(checkout);
    r.setStatus(ReservationStatus.PENDING);
    r.setStayDuration((int) days);
    r.setCreatedAt(LocalDateTime.now().minusHours(3));
    r = reservationRepository.save(r);

    reservationRoomRepository.save(
        new ReservationRoom(new ReservationRoomId(r.getId(), room.getId()), room, r));

    Payment p = new Payment();
    p.setReservation(r);
    p.setMethod(method);
    p.setAmount(room.getRoomType().getBasePrice() * (int) days);
    p.setPaymentDate(null);                 // unpaid until approved
    paymentRepository.save(p);
  }
}
