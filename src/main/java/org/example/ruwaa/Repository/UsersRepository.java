package org.example.ruwaa.Repository;

import org.example.ruwaa.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer>
{


    @Query("select u from Users u where u.email =:email")
    Optional<Users> findUserByEmail(String email);

    @Query("select u from Users u where u.id = :id")
    Optional<Users> findUserById(Integer id);

    @Query("select u from Users u where u.username = :username")
    Optional<Users> findUserByUsername(String username);

    @Query("select u from Users u join u.message m where m.id = :id")
    Users findUserByMessageId(Integer id);
}
