package hotel_management.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import hotel_management.demo.dto.LoginRequest;
import hotel_management.demo.dto.RegisterGuestRequest;
import hotel_management.demo.dto.AuthResponse;
import hotel_management.demo.service.AccountService;
import hotel_management.demo.service.GuestService;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

  @Autowired
  private AccountService accountService;

  @Autowired
  private GuestService guestService;

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
    try {
      AuthResponse response = accountService.login(request);
      return ResponseEntity.ok(response);
    } catch (RuntimeException e) {
      AuthResponse error = new AuthResponse();
      error.setMessage(e.getMessage());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
  }

  @PostMapping("/register/guest")
  public ResponseEntity<AuthResponse> registerGuest(@RequestBody RegisterGuestRequest request) {
    try {
      AuthResponse response = guestService.registerGuest(request);
      return ResponseEntity.ok(response);
    } catch (RuntimeException e) {
      AuthResponse error = new AuthResponse();
      error.setMessage(e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
  }

  @PostMapping("/refresh")
  public ResponseEntity<AuthResponse> refreshToken(@RequestBody String oldToken) {
    AuthResponse response = accountService.refreshToken(oldToken);
    return ResponseEntity.ok(response);
  }
}
