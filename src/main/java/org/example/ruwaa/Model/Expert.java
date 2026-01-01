package org.example.ruwaa.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Expert
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String linkedin_url;

    private Integer review_count;

    private Double consult_price;

    private Boolean isAvailable;

    private Boolean isActive;

    private byte[] data;

    private Double count_rating;

    private Double total_rating;





    @OneToOne
    @MapsId
    @JsonIgnore
    private Users users;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "expert")
    @JsonIgnore
    private Set<Review> reviews;

    @ManyToOne
    @JsonIgnore
    private Categories category;
}
