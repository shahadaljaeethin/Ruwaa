package org.example.ruwaa.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Post
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //private String title;

    private String content;

    private Integer views;
    @Pattern(regexp = "^(public_work|private_work|subscription_content|free_content)$",message = "role can be admin or user")
    private String type;

    @PastOrPresent
    private LocalDateTime publishAt;

    //new
    @ManyToMany(mappedBy = "requestedPrivateWorks")
    @JsonIgnore
    private List<Users> permitWorkVisiablity;


    @ManyToOne
    @JsonIgnore
    private Users users;

    @OneToMany(cascade=CascadeType.ALL,mappedBy = "post")
    @JsonIgnore
    private List<Review> reviews;

    @OneToOne(cascade = CascadeType.ALL,mappedBy = "post")
    private Attachments attachment;

    @ManyToOne
    @JsonIgnore
    private Categories category;
}
