package edu.yuriikoval1997.flightbooking.entities;

import java.util.List;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "aircraft")
public class Aircraft {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "model", nullable = false, length = 25)
    private String model;
    @Column(name = "capacity", nullable = false)
    private Short capacity;
    @OneToMany(mappedBy = "aircraft")
    private List<Flight> flights;
}
