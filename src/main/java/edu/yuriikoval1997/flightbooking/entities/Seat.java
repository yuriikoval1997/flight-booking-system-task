package edu.yuriikoval1997.flightbooking.entities;

import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "seats")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seat_row", nullable = false)
    private Short seatRow;

    @Column(name = "seat_col", nullable = false)
    private Short seatCol;

    @ManyToOne
    @JoinColumn(name = "booking_id", referencedColumnName = "id")
    private Booking booking;

    public Seat(Short seatRow, Short seatCol, Booking booking) {
        this.seatRow = seatRow;
        this.seatCol = seatCol;
        this.booking = booking;
    }
}

