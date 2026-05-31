package hotel_management.demo.service.mapper;

import org.springframework.stereotype.Component;

import hotel_management.demo.dto.RoomTypeDTO;
import hotel_management.demo.schema.RoomType;

@Component
public class RoomTypeMapper {
    public RoomTypeDTO toDTO(RoomType roomType) {
        if (roomType == null) return null;

        return new RoomTypeDTO(
            roomType.getId(),
            roomType.getName(),
            roomType.getDescription(),
            roomType.getCapacity(),
            roomType.getBasePrice()
        );
    }
}

    