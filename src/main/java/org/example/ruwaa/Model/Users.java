package org.example.ruwaa.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Users implements UserDetails
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String about_me;

    private String name;
    @Column(columnDefinition = "varchar(20) not null", unique = true)
    private String username;

    private String password;
    @Column(columnDefinition = "varchar(50) not null", unique = true)
    private String email;

    @Column(columnDefinition = "varchar(13) not null", unique = true)
    private String phone;
    @Column(columnDefinition = "varchar(10) not null")
    private String role;

    private LocalDateTime createdAt;

    @PositiveOrZero
    private Double balance;



    @OneToOne(cascade = CascadeType.ALL, mappedBy = "users")
    @PrimaryKeyJoinColumn
    private Customer customer;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "users")
    @PrimaryKeyJoinColumn
    private Expert expert;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "users")
    private List<Post> posts;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "users")
    @JsonIgnore
    private Set<Message> message;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "users", orphanRemoval = true)
    private List<Card> cards;



    //new
    @ManyToMany
    @JsonIgnore
    private Set<Post> requestedPrivateWorks;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(this.role));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
