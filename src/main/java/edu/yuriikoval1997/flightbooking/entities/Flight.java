package edu.yuriikoval1997.flightbooking.entities;

import java.time.ZonedDateTime;
import java.util.List;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
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
}


