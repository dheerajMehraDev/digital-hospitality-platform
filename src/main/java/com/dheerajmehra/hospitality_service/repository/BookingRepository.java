package com.dheerajmehra.hospitality_service.repository;


import com.dheerajmehra.hospitality_service.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
