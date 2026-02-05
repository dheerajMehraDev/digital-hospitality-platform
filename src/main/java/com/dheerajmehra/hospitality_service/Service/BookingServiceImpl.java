package com.dheerajmehra.hospitality_service.Service;


import com.dheerajmehra.hospitality_service.dto.BookingDto;
import com.dheerajmehra.hospitality_service.dto.BookingRequest;
import com.dheerajmehra.hospitality_service.dto.GuestDto;
import com.dheerajmehra.hospitality_service.dto.PaymentVerifyRequest;
import com.dheerajmehra.hospitality_service.entity.*;
import com.dheerajmehra.hospitality_service.entity.enums.BookingStatus;
import com.dheerajmehra.hospitality_service.entity.enums.PaymentStatus;
import com.dheerajmehra.hospitality_service.exception.ResourceNotFoundException;
import com.dheerajmehra.hospitality_service.exception.UnAuthorizedException;
import com.dheerajmehra.hospitality_service.repository.*;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService{
    private final GuestRepository guestRepository;
    private final ModelMapper modelMapper;

    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;
    private final PaymentRepository paymentRepository;

    @Value("${razorpay.api.key}")
    private String apiKey;

    @Value("${razorpay.api.secret}")
    private String apiSecret;

    @Value("${razorpay.webhook.secret}")
    private String webhookSecret;

    @Override
    @Transactional
    public BookingDto initialiseBooking(BookingRequest bookingRequest) {

        log.info("Initialising booking for hotel : {}, room: {}, date {}-{}", bookingRequest.getHotelId(),
                bookingRequest.getRoomId(), bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate());

        Hotel hotel = hotelRepository.findById(bookingRequest.getHotelId()).orElseThrow(() ->
                new ResourceNotFoundException("Hotel not found with id: "+bookingRequest.getHotelId()));

        Room room = roomRepository.findById(bookingRequest.getRoomId()).orElseThrow(() ->
                new ResourceNotFoundException("Room not found with id: "+bookingRequest.getRoomId()));

        List<Inventory> inventoryList = inventoryRepository.findAndLockAvailableInventory(room.getId(),
                bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate(), bookingRequest.getRoomsCount());

        long daysCount = ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate())+1;

        if (inventoryList.size() != daysCount) {
            throw new IllegalStateException("Room is not available anymore");
        }

        // Reserve the room/ update the booked count of inventories

        for(Inventory inventory: inventoryList) {
            inventory.setReservedCount(inventory.getReservedCount() + bookingRequest.getRoomsCount());
        }

        inventoryRepository.saveAll(inventoryList);

        // Create the Booking



        // TODO: calculate dynamic amount

        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .user(getCurrentUser())
                .roomsCount(bookingRequest.getRoomsCount())
                .amount(BigDecimal.TEN)
                .build();

        booking = bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDto.class);
    }

    @Override
    @Transactional
    public BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList) {

        log.info("Adding guests for booking with id: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new ResourceNotFoundException("Booking not found with id: "+bookingId));

        User user = getCurrentUser();
        if(!user.equals(booking.getUser())){
            throw new UnAuthorizedException("user is not authorized to book this booking");
        }


        if (hasBookingExpired(booking)) {
            throw new IllegalStateException("Booking has already expired");
        }

        if(booking.getBookingStatus() != BookingStatus.RESERVED) {
            throw new IllegalStateException("Booking is not under reserved state, cannot add guests");
        }

        for (GuestDto guestDto: guestDtoList) {
            Guest guest = modelMapper.map(guestDto, Guest.class);
            guest.setUser(getCurrentUser());
            guest = guestRepository.save(guest);
            booking.getGuests().add(guest);
        }

        booking.setBookingStatus(BookingStatus.GUESTS_ADDED);
        booking = bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDto.class);
    }

    @Override
    public String initiliatePayment(Long bookingId) {
      /* Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("booking not found for the id " + bookingId));

       User user = getCurrentUser();
       if(!user.equals(booking.getUser()))
            throw new UnAuthorizedException("user is not authorize for this booking");
       if(hasBookingExpired(booking))
           throw new UnAuthorizedException("booking has expired for the booking id " + bookingId);*/
//        int amount = booking.getAmount().intValue();


        try {
            return createOrder(1000,"INR",bookingId.toString());
        } catch (RazorpayException e) {
            throw new RuntimeException(e);
        }
    }

    public String createOrder(int amount, String currency, String bookingId)
            throws RazorpayException {

        RazorpayClient razorpayClient = new RazorpayClient(apiKey, apiSecret);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount);
        orderRequest.put("currency", currency);
        orderRequest.put("receipt", "BOOKING_" + bookingId);

        Booking booking = bookingRepository.findById(Long.valueOf(bookingId))
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        Order order = razorpayClient.orders.create(orderRequest);

        String razorpayOrderId = order.get("id");

        Payment payment = Payment.builder()
                .paymentStatus(PaymentStatus.PENDING)
                .amount(order.get("amount"))
                .booking(booking)
                .transactionId(razorpayOrderId)   // real id
                .build();

        paymentRepository.save(payment);

        return order.toString();
    }


    @Override
    public void verifyPayment(PaymentVerifyRequest request) {
        String orderId = request.getRazorpayOrderId();
        String paymentId = request.getRazorpayPaymentId();
        String razorpaySignature = request.getRazorpaySignature();


        Payment payment = paymentRepository
                .findByTransactionId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        boolean isValid = false;
        try {
            isValid = Utils.verifySignature(orderId, paymentId, razorpaySignature);
        } catch (RazorpayException e) {
            throw new RuntimeException(e);
        }

        if (!isValid) {
            payment.setPaymentStatus(PaymentStatus.CANCELLED);
            paymentRepository.save(payment);
            throw new RuntimeException("Payment verification failed");
        }

        payment.setPaymentStatus(PaymentStatus.CONFIRMED);
        payment.setPaymentId(paymentId);

        // TODO : set the booking status confirmed and reduce the inventory or other things related to this


        paymentRepository.save(payment);
    }

    @Override
    public boolean webHook(String payload, String signature) {
        try {
            // 1. Verify webhook signature
            Utils.verifyWebhookSignature(payload, signature, webhookSecret);

            JSONObject json = new JSONObject(payload);
            String event = json.getString("event");

            JSONObject paymentEntity =
                    json.getJSONObject("payload")
                            .getJSONObject("payment")
                            .getJSONObject("entity");

            String orderId = paymentEntity.getString("order_id");

            Payment payment = paymentRepository
                    .findByTransactionId(orderId)
                    .orElseThrow(() -> new RuntimeException("Payment not found"));

            // 2. Handle events
            if ("payment.captured".equals(event)) {

                payment.setPaymentStatus(PaymentStatus.CONFIRMED);
                payment.setPaymentId(paymentEntity.getString("id"));

//                Booking booking = payment.getBooking();
//                booking.setStatus(BookingStatus);

//                bookingRepository.save(booking);
                paymentRepository.save(payment);
            }

            if ("payment.failed".equals(event)) {
                payment.setPaymentStatus(PaymentStatus.CANCELLED);
                paymentRepository.save(payment);
            }
            return true;


        } catch (Exception e) {
           throw new RuntimeException("paymnet verification failed through webhook");
        }
    }


    public boolean hasBookingExpired(Booking booking) {
        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }

    public User getCurrentUser() {

        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
