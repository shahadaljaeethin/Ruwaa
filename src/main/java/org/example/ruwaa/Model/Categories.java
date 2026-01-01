package org.example.ruwaa.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Categories
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //@Size(min = 4,message = "minimum length is 4")
    @Column(columnDefinition = "varchar(30) unique not null")
    private String name;

    @OneToMany(mappedBy = "category")
    private List<Expert> expertList;

    @OneToMany(mappedBy = "category")
    private List<Post> posts;
}
