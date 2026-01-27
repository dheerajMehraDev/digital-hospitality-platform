package com.dheerajmehra.hospitality_service.Service;


import com.dheerajmehra.hospitality_service.entity.Room;

public interface InventoryService {

    void initializeRoomForAYear(Room room);

    void deleteFutureInventories(Room room);

}
