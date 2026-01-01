package org.example.ruwaa.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ruwaa.Api.ApiResponse;
import org.example.ruwaa.Model.Categories;
import org.example.ruwaa.Repository.CategoriesRepository;
import org.example.ruwaa.Service.CategoriesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/category")
@RequiredArgsConstructor
public class CategoriesController
{
    private final CategoriesService categoriesService;

    @PostMapping("/create")
    public ResponseEntity<?> createCategory(@RequestBody @Valid Categories categories){
        categoriesService.addCategory(categories);
        return ResponseEntity.status(200).body(new ApiResponse("category created"));
    }

    @GetMapping("/get")
    public ResponseEntity<?> findAll(){
        return ResponseEntity.status(200).body(categoriesService.findAll());
    }

    @GetMapping("/get/{name}")
    public ResponseEntity<?> findCategoryByName(@PathVariable String name){
        return ResponseEntity.status(200).body(categoriesService.getCategoryByName(name));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Integer id, @RequestBody @Valid Categories categories){
        categoriesService.updateCategory(id, categories);
        return ResponseEntity.status(200).body(new ApiResponse("category updated"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Integer id){
        categoriesService.deleteCategory(id);
        return ResponseEntity.status(200).body(new ApiResponse("category deleted"));
    }
}
