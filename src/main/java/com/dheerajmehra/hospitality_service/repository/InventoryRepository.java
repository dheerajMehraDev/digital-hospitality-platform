package com.dheerajmehra.hospitality_service.repository;


import com.dheerajmehra.hospitality_service.entity.Inventory;
import com.dheerajmehra.hospitality_service.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    void deleteByDateAfterAndRoom(LocalDate date, Room room);
}
