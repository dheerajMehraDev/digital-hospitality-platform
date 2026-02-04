package com.dheerajmehra.hospitality_service.Security;

import com.dheerajmehra.hospitality_service.dto.LoginRequestDto;
import com.dheerajmehra.hospitality_service.dto.SignupRequestDto;
import com.dheerajmehra.hospitality_service.dto.SignupResponseDto;
import com.dheerajmehra.hospitality_service.entity.User;
import com.dheerajmehra.hospitality_service.entity.enums.Role;
import com.dheerajmehra.hospitality_service.exception.ResourceNotFoundException;
import com.dheerajmehra.hospitality_service.exception.UnAuthorizedException;
import com.dheerajmehra.hospitality_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


   public SignupResponseDto signUp(SignupRequestDto dto){
        User user = userRepository.findByEmail(dto.getEmail()).orElse(null);
        if(user != null){
            throw new RuntimeException("user already exists for this email");
        }
        User newUser = modelMapper.map(dto,User.class);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser.setRoles(Set.of(Role.GUEST));
        User savedUser = userRepository.save(newUser);
        return modelMapper.map(savedUser,SignupResponseDto.class);
    }

    public String[] login(LoginRequestDto dto){
       String[] response = new String [2];
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(),dto.getPassword())
        );

        User user = (User) authentication.getPrincipal();
        response[0] = jwtService.getSecretToken(user);
        response[1] = jwtService.getRefreshToken(user);

        return response;

    }

    public String refreshToken(String refreshToken){
       Long userId = jwtService.getUserIdFromToken(refreshToken);
       if(userId == null){
           throw new UnAuthorizedException("not authorized");
       }
       User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user not found for the user id " + userId));

       return jwtService.getSecretToken(user);
    }


}
