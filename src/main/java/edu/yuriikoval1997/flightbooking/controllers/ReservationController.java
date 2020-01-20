package edu.yuriikoval1997.flightbooking.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReservationController {

    @GetMapping("seats")
    public ResponseEntity<int[][]> getAllSeats() {
        return null;
    }

    @PutMapping("seats/reservation")
    public ResponseEntity<Object> makeReservation() {
        return null;
    }
}
