package com.dheerajmehra.hospitality_service.Service;



import com.dheerajmehra.hospitality_service.dto.BookingDto;
import com.dheerajmehra.hospitality_service.dto.BookingRequest;
import com.dheerajmehra.hospitality_service.dto.GuestDto;

import java.util.List;

public interface BookingService {

    BookingDto initialiseBooking(BookingRequest bookingRequest);

    BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList);
}
