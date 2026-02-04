package com.dheerajmehra.hospitality_service.Service;

import com.dheerajmehra.hospitality_service.entity.User;
import com.dheerajmehra.hospitality_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.lang.module.ResolutionException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService , UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public User getUserByUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResolutionException("user is not found for the user id " + userId));
    }

    @Override
    public @Nullable UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElse(null);
    }
}
