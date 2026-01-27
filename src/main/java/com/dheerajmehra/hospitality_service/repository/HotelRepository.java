package com.dheerajmehra.hospitality_service.repository;


import com.dheerajmehra.hospitality_service.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
}
