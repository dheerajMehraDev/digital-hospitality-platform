package com.dheerajmehra.hospitality_service.Security;

import com.dheerajmehra.hospitality_service.dto.LoginRequestDto;
import com.dheerajmehra.hospitality_service.dto.LoginResponseDto;
import com.dheerajmehra.hospitality_service.dto.SignupRequestDto;
import com.dheerajmehra.hospitality_service.dto.SignupResponseDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signup(@RequestBody SignupRequestDto dto){

        SignupResponseDto responseDto = authService.signUp(dto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto dto , HttpServletRequest request , HttpServletResponse response){
        String[] tokens = authService.login(dto);
        Cookie cookie = new Cookie("refreshToken" , tokens[1]);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        LoginResponseDto loginResponseDto = new LoginResponseDto(tokens[0]);
        return new ResponseEntity<>(loginResponseDto,HttpStatus.OK);
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<LoginResponseDto> refreshToken(HttpServletRequest request){
        String refreshToken = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("refreshToken"))
                .findFirst()
                .map(cookie -> cookie.getValue())
                .orElseThrow(() -> new AuthenticationServiceException("refresh token not found"));

        String accessToken = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(new LoginResponseDto(accessToken));
    }


}
