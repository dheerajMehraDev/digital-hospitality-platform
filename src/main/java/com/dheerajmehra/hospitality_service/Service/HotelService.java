package com.dheerajmehra.hospitality_service.Service;


import com.dheerajmehra.hospitality_service.dto.HotelDto;
import com.dheerajmehra.hospitality_service.dto.HotelInfoDto;

public interface HotelService {
    HotelDto createNewHotel(HotelDto hotelDto);

    HotelDto getHotelById(Long id);

    HotelDto updateHotelById(Long id, HotelDto hotelDto);

    void deleteHotelById(Long id);

    void activateHotel(Long hotelId);

    HotelInfoDto getHotelInfoById(Long hotelId);
}
