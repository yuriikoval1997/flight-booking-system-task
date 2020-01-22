package edu.yuriikoval1997.flightbooking.entities;

import java.util.List;
import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;

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

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.PERSIST)
    private List<Seat> seats;

    public Booking(Flight flight, Integer price) {
        this.flight = flight;
        this.price = price;
    }
}
