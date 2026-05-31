package hotel_management.demo.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import hotel_management.demo.dto.EmployeeDTO;
import hotel_management.demo.dto.HotelDTO;
import hotel_management.demo.dto.RoomDTO;
import hotel_management.demo.dto.RoomTypeDTO;
import hotel_management.demo.repository.EmployeeRepository;
import hotel_management.demo.repository.HotelRepository;
import hotel_management.demo.repository.RoomRepository;
import hotel_management.demo.schema.Hotel;
import hotel_management.demo.schema.Room;
import hotel_management.demo.schema.Employee;
import hotel_management.demo.schema.RoomType;
import hotel_management.demo.service.mapper.EmployeeMapper;
import hotel_management.demo.service.mapper.RoomMapper;
import hotel_management.demo.service.mapper.RoomTypeMapper;

@Service
public class HotelService {

  private final EmployeeRepository employeeRepository;
  private final HotelRepository hotelRepository;
  private final RoomRepository roomRepository;
  private final RoomMapper roomMapper;
  private final EmployeeMapper employeeMapper;
  private final RoomTypeMapper roomTypeMapper;

  public HotelService(
      HotelRepository hotelRepository,
      RoomRepository roomRepository,
      EmployeeRepository employeeRepository,
      RoomMapper roomMapper,
      EmployeeMapper employeeMapper,
      RoomTypeMapper roomTypeMapper) {
    this.hotelRepository = hotelRepository;
    this.roomRepository = roomRepository;
    this.employeeRepository = employeeRepository;
    this.employeeMapper = employeeMapper;
    this.roomMapper = roomMapper;
    this.roomTypeMapper = roomTypeMapper;
  }

  public Hotel createHotel(Hotel hotel) {
    if (hotel.getName() == null || hotel.getName().isBlank()) {
      throw new IllegalArgumentException("Hotel name must be provided.");
    }
    return hotelRepository.save(hotel);
  }

  public Hotel getHotelById(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("Hotel ID cannot be null");
    }
    return hotelRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Hotel not found with ID: " + id));
  }

  public List<Hotel> getAllHotels() {
    return hotelRepository.findAll();
  }

  public Hotel updateHotel(UUID id, Hotel hotelDetails) {
    Hotel existingHotel = getHotelById(id);

    existingHotel.setName(hotelDetails.getName());
    existingHotel.setRating(hotelDetails.getRating());
    existingHotel.setPhone(hotelDetails.getPhone());
    existingHotel.setEmail(hotelDetails.getEmail());
    existingHotel.setAddress(hotelDetails.getAddress());

    return hotelRepository.save(existingHotel);
  }

  public void deleteHotel(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("Hotel ID cannot be null");
    }
    if (!hotelRepository.existsById(id)) {
      throw new EntityNotFoundException("Hotel not found with ID: " + id);
    }
    hotelRepository.deleteById(id);
  }

  public HotelDTO toDTO(Hotel hotel) {
    if (hotel == null)
      return null;

    List<Room> rooms = roomRepository.getRoomsByHotelId(hotel.getId());

    List<Employee> employees = employeeRepository.getEmployeeByHotelId(hotel.getId());

    List<RoomDTO> roomDTOs = rooms.stream()
        .map(roomMapper::toDTO)
        .collect(Collectors.toList());

    List<EmployeeDTO> employeeDTOs = employees.stream()
        .map(employeeMapper::toDTO)
        .collect(Collectors.toList());

    List<RoomType> roomTypeList = rooms.stream()
        .map(Room::getRoomType)
        .distinct()
        .collect(Collectors.toList());

    List<RoomTypeDTO> roomTypeDTOs = roomTypeList.stream()
        .map(roomTypeMapper::toDTO)
        .collect(Collectors.toList());

    return new HotelDTO(
        hotel.getId(),
        hotel.getName(),
        hotel.getRating(),
        hotel.getPhone(),
        hotel.getEmail(),
        hotel.getAddress(),
        roomDTOs,
        roomTypeDTOs,
        employeeDTOs
    );
  }
}