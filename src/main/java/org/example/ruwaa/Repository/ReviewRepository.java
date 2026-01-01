package org.example.ruwaa.Repository;

import org.example.ruwaa.Model.Expert;
import org.example.ruwaa.Model.Post;
import org.example.ruwaa.Model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer>
{
    @Query("select r from Review r where r.id =:id")
    Optional<Review> findReviewById(Integer id);

    @Query("select r from Review r where r.status = 'Accepted' ")
    List<Review> findUnfinishedReviews();

    @Query("select r from Review r where r.status = 'Completed'  ")
    List<Review> findFinishedReviews();

    @Query("select r from Review r join r.expert e where e = :expert")
    List<Review> findAllByExpert(Expert expert);

    List<Review> findAllByPost(Post post);

    @Query("select r from Review r where r.status = 'Pending' and r.expert = ?1")
    List<Review> findPendingReviews (Expert expert);

    @Query("select r from Review r join r.post p where p.id = :id and r.status = 'Completed' ")
    List<Review> findCompletedReviewsOfPost(Integer id);

    @Query("select e from Expert e join e.reviews r where r.id =:id")
    Optional<Expert> findExpertByReviewId(Integer id);

}
