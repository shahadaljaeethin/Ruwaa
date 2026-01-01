package org.example.ruwaa.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Review
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String content;

    private Integer feedback_rating;

    @Pattern(regexp="^(Pending|Completed|Rejected|Accepted)$")
    private String status;

    private Boolean hasRated;

    @ManyToOne
    @JsonIgnore
    private Expert expert;

    @ManyToOne
    @JsonIgnore
    private Post post;

    @OneToOne(cascade = CascadeType.ALL,mappedBy = "review")
    @JsonIgnore
    private Chat chat;
}
