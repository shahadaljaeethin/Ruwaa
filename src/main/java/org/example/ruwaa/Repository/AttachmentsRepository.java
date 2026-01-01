package org.example.ruwaa.Repository;

import org.example.ruwaa.Model.Attachments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttachmentsRepository extends JpaRepository<Attachments, Integer>
{
    @Query("select a from Attachments a join a.post p where p.id = :id")
    Optional<Attachments> findAttachmentOfPost(Integer id);
}
