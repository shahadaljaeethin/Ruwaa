package org.example.ruwaa.Service;

import lombok.RequiredArgsConstructor;
import org.example.ruwaa.Api.ApiException;
import org.example.ruwaa.Model.Categories;
import org.example.ruwaa.Repository.CategoriesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoriesService {

    private final CategoriesRepository categoriesRepository;

    public List<Categories> findAll() {
        List<Categories> categories = categoriesRepository.findAll();
        if (categories.isEmpty()) {
            throw new ApiException("no categories found");
        }
        return categories;
    }

    public Categories getCategoryByName(String name){
        return categoriesRepository.findCategoryByName(name).orElseThrow(() -> new ApiException("no category have this name: "+name));
    }

    public void addCategory(Categories categories){
        if (categoriesRepository.findCategoryByName(categories.getName()).isPresent()) {
            throw new ApiException("category already exists");
        }
        categoriesRepository.save(categories);
    }

    public void updateCategory(Integer id, Categories categories){
        Categories c = categoriesRepository.findCategoryById(id).orElseThrow(() -> new ApiException("category not found"));
        c.setName(categories.getName());
        categoriesRepository.save(c);
    }

    public void deleteCategory(Integer id){
        Categories c = categoriesRepository.findCategoryById(id).orElseThrow(() -> new ApiException("category not found"));
        categoriesRepository.delete(c);
    }
}
