package hotel_management.demo.controller;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PageController {

  @GetMapping("/")
  public String authentication(Model model) {
    model.addAttribute("title", "Authentication Page");
    return "authentication";
  }

  @GetMapping("/homepage")
  public String index(Model model) {
    model.addAttribute("title", "Home Page");
    return "index";
  }

  @GetMapping("/hotels")
  public String hotels(Model model) {
    model.addAttribute("title", "Hotels Page");
    return "hotels";
  }

  @GetMapping("/client")
  public String client(Model model) {
    model.addAttribute("title", "Guest Page");
    return "client";
  }

  @GetMapping("/reservation")
  public String reservation(Model model) {
    model.addAttribute("title", "Reservation Page");
    return "reservation";
  }

  @GetMapping("/hotels/{hotelId}")
  public String getHotelById(@PathVariable UUID hotelId, Model model) {
    model.addAttribute("hotelId", hotelId);
    return "room";
  }

  @GetMapping("/setting")
  public String setting(Model model) {
    model.addAttribute("title", "Employee Management");
    return "setting";
  }

  @GetMapping("/employee")
  public String employee(Model model) {
    model.addAttribute("title", "Employee Management");
    return "setting";
  }

  @GetMapping("/admin")
  public String admin(Model model) {
    model.addAttribute("title", "Admin Page");
    return "index";
  }

  @GetMapping("/guest")
  public String guest(Model model) {
    model.addAttribute("title", "Guest Page");
    return "guest";
  }

  @GetMapping("/payments/{reservationId}")
  public String getPaymentById(@PathVariable UUID reservationId, Model model) {
    model.addAttribute("reservationId", reservationId);
    return "payment";
  }
  
}

