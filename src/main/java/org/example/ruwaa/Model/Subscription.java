package org.example.ruwaa.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Subscription
{
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDateTime subscription_date;

    private LocalDateTime end_date;

    @OneToOne
    @MapsId
    @JsonIgnore
    private Customer customer;
}
