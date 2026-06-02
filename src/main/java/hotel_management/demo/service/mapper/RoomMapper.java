package hotel_management.demo.service.mapper;

import org.springframework.stereotype.Component;

import hotel_management.demo.dto.RoomDTO;
import hotel_management.demo.schema.Room;

@Component
public class RoomMapper {
    public RoomDTO toDTO(Room room) {
        if (room == null) return null;

        return new RoomDTO(
            room.getId(),
            room.getFloor(),
            room.getRoomNumber(),
            room.getNote(),
            room.getStatus(),
            room.getHotel().getId(),
            room.getHotel().getName(),
            room.getRoomType().getId(),
            room.getRoomType().getName(),
            room.getRoomType().getBasePrice()
      );
    }
}

    