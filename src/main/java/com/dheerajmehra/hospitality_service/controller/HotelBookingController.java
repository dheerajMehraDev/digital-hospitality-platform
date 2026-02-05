package com.dheerajmehra.hospitality_service.controller;


import com.dheerajmehra.hospitality_service.Service.BookingService;
import com.dheerajmehra.hospitality_service.dto.BookingDto;
import com.dheerajmehra.hospitality_service.dto.BookingRequest;
import com.dheerajmehra.hospitality_service.dto.GuestDto;
import com.dheerajmehra.hospitality_service.dto.PaymentVerifyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class HotelBookingController {

    private final BookingService bookingService;

    @PostMapping("/init")
    public ResponseEntity<BookingDto> initialiseBooking(@RequestBody BookingRequest bookingRequest) {
        return ResponseEntity.ok(bookingService.initialiseBooking(bookingRequest));
    }

    @PostMapping("/{bookingId}/addGuests")
    public ResponseEntity<BookingDto> addGuests(@PathVariable Long bookingId,
                                                @RequestBody List<GuestDto> guestDtoList) {
        return ResponseEntity.ok(bookingService.addGuests(bookingId, guestDtoList));
    }

    @PostMapping("/payments/{bookingId}")
    public ResponseEntity<Map<String,String>> payment(@PathVariable Long bookingId){
       String sessionUrl =  bookingService.initiliatePayment(bookingId);
       return ResponseEntity.ok(Map.of("sessionUrl" , sessionUrl));
    }
    @PostMapping("/payments/verify")
    public ResponseEntity<?> verify(@RequestBody PaymentVerifyRequest request){
        bookingService.verifyPayment(
               request
        );
        return ResponseEntity.ok("payment verification success");
    }

    @PostMapping("payments/webhook/razorpay")
    public ResponseEntity<String> webhook(  @RequestBody String payload,
                                       @RequestHeader("X-Razorpay-Signature") String signature){
       boolean status =  bookingService.webHook(payload,signature);
       return ResponseEntity.ok("verification successful");
    }

}
