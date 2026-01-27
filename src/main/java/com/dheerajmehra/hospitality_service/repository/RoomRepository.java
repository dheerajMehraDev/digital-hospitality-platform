package com.dheerajmehra.hospitality_service.repository;


import com.dheerajmehra.hospitality_service.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
