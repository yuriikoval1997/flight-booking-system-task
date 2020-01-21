package edu.yuriikoval1997.flightbooking.entities;

import java.time.ZonedDateTime;
import java.util.List;
import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "flights")
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Aircraft aircraft;

    @Column(name = "departure_time", nullable = false)
    private ZonedDateTime departure;

    @Column(name = "arrival_time", nullable = false)
    private ZonedDateTime arrival;

    @Column(name = "departure_from", nullable = false)
    private String from;

    @Column(name = "arrival_to", nullable = false)
    private String to;

    @OneToMany(mappedBy = "flight")
    private List<Booking> bookings;

    public Flight(Aircraft aircraft, ZonedDateTime departure, ZonedDateTime arrival, String from, String to) {
        this.aircraft = aircraft;
        this.departure = departure;
        this.arrival = arrival;
        this.from = from;
        this.to = to;
    }
}


