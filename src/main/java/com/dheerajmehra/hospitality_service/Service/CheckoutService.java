package com.dheerajmehra.hospitality_service.Service;

import com.dheerajmehra.hospitality_service.entity.Booking;

public interface CheckoutService {

    String getCheckoutSession(Booking booking, String successUrl, String failureUrl);

}