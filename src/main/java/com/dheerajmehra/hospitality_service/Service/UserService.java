package com.dheerajmehra.hospitality_service.Service;

import com.dheerajmehra.hospitality_service.entity.User;

public interface UserService {
    User getUserByUserId(Long userId);
}
