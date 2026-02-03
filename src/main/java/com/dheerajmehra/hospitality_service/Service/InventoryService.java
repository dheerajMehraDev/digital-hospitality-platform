package com.dheerajmehra.hospitality_service.Service;


import com.dheerajmehra.hospitality_service.dto.HotelPriceDto;
import com.dheerajmehra.hospitality_service.dto.HotelSearchRequest;
import com.dheerajmehra.hospitality_service.entity.Room;
import org.springframework.data.domain.Page;

public interface InventoryService {

    void initializeRoomForAYear(Room room);

    void deleteAllInventories(Room room);

    Page<HotelPriceDto> searchHotels(HotelSearchRequest hotelSearchRequest);
}
