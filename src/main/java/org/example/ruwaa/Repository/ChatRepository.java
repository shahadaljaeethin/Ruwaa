package org.example.ruwaa.Repository;

import org.example.ruwaa.Model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer>
{
    @Query("select c from Chat c where c.id =:id")
    Optional<Chat> findChatById(Integer id);


}
