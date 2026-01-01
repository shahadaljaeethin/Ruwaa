package org.example.ruwaa.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ruwaa.Api.ApiResponse;
import org.example.ruwaa.Model.Card;
import org.example.ruwaa.Service.CardsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/card")
@RequiredArgsConstructor
public class CardsController
{
    private final CardsService cardsService;

    @PostMapping("/add")
    public ResponseEntity<?> addCard(@RequestBody @Valid Card card, Authentication auth){
        cardsService.addCard(card,auth.getName());
        return ResponseEntity.status(200).body(new ApiResponse("card added successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCard(@PathVariable Integer id, Authentication auth){
        cardsService.deleteCard(id,auth.getName());
        return ResponseEntity.status(200).body(new ApiResponse("card deleted successfully"));
    }
}
