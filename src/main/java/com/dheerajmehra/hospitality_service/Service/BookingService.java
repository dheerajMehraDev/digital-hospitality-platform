package com.dheerajmehra.hospitality_service.Service;



import com.dheerajmehra.hospitality_service.dto.BookingDto;
import com.dheerajmehra.hospitality_service.dto.BookingRequest;
import com.dheerajmehra.hospitality_service.dto.GuestDto;
import com.dheerajmehra.hospitality_service.dto.PaymentVerifyRequest;

import java.util.List;

public interface BookingService {

    BookingDto initialiseBooking(BookingRequest bookingRequest);

    BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList);

    String initiliatePayment(Long bookingId);

    void verifyPayment(PaymentVerifyRequest request);

    boolean webHook(String payload , String signature);
}
