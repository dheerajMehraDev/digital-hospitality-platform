package com.dheerajmehra.hospitality_service.controller;


import com.dheerajmehra.hospitality_service.Service.HotelService;
import com.dheerajmehra.hospitality_service.Service.InventoryService;
import com.dheerajmehra.hospitality_service.dto.HotelInfoDto;
import com.dheerajmehra.hospitality_service.dto.HotelPriceDto;
import com.dheerajmehra.hospitality_service.dto.HotelSearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelBrowseController {

    private final InventoryService inventoryService;
    private final HotelService hotelService;

    @GetMapping("/search")
    public ResponseEntity<Page<HotelPriceDto>> searchHotels(@RequestBody HotelSearchRequest hotelSearchRequest) {

        var page = inventoryService.searchHotels(hotelSearchRequest);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{hotelId}/info")
    public ResponseEntity<HotelInfoDto> getHotelInfo(@PathVariable Long hotelId) {
        return ResponseEntity.ok(hotelService.getHotelInfoById(hotelId));
    }

}
