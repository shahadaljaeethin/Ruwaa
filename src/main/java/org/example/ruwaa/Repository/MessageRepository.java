package org.example.ruwaa.Repository;

import org.example.ruwaa.Model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer>
{
    @Query("select m from Message m where m.id =:id")
    Optional<Message> findMessageById(Integer id);

    @Query("select m from Message m join m.chat c where c.id = :id order by m.id asc ")
    Optional<List<Message>> findAllMessagesByChatId(Integer id);

    Integer id(Integer id);
}
