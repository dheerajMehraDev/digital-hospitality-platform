package com.dheerajmehra.hospitality_service.repository;


import com.dheerajmehra.hospitality_service.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {
}