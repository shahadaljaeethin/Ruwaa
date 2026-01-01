package org.example.ruwaa.Repository;

import lombok.Locked;
import org.example.ruwaa.Model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer>
{
    @Query("select m from Post m where m.id =:id")
    Optional<Post> findPostById(Integer id);

    List<Post> findPostByUsers_id(Integer user_id);

    @Query("select p from Post p where p.type = :type")
    List<Post> findPostByType(String type);

    @Query("  SELECT p FROM Post p WHERE p.type = 'subscription_content' AND p.publishAt >= :fromDate AND p.views >= :minViews")
    List<Post> getRecentMonthLearningPosts(LocalDateTime fromDate, Integer minViews);

    @Query("select p from Post p join p.users u where u.id = :id")
    List<Post> getUserPosts(Integer id);

}
