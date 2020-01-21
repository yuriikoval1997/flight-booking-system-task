package edu.yuriikoval1997.flightbooking.entities;

import java.util.List;
import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", referencedColumnName = "id")
    private Flight flight;

    @Column(name = "price", nullable = false)
    private Integer price;

    @OneToMany(mappedBy = "booking")
    private List<Seat> seats;

    public Booking(Flight flight, Integer price, List<Seat> seats) {
        this.flight = flight;
        this.price = price;
        this.seats = seats;
    }
}
