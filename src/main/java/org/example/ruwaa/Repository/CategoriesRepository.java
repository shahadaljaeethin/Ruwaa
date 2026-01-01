package org.example.ruwaa.Repository;

import org.example.ruwaa.Model.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriesRepository extends JpaRepository<Categories, Integer>
{
    @Query("select c from Categories c where c.name =:name")
    Optional<Categories> findCategoryByName(String name);

    @Query("select c from Categories c where c.id =:id")
    Optional<Categories> findCategoryById(Integer id);
}
