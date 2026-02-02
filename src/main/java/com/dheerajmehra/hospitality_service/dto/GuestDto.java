package com.dheerajmehra.hospitality_service.dto;


import com.dheerajmehra.hospitality_service.entity.User;
import com.dheerajmehra.hospitality_service.entity.enums.Gender;
import lombok.Data;

@Data
public class GuestDto {
    private Long id;
    private User user;
    private String name;
    private Gender gender;
    private Integer age;
}
