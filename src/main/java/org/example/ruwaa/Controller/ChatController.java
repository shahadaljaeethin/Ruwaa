package org.example.ruwaa.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ruwaa.Api.ApiResponse;
import org.example.ruwaa.Model.Chat;
import org.example.ruwaa.Service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static org.springframework.security.authorization.AuthorityReactiveAuthorizationManager.hasRole;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController
{
    private final ChatService chatService;


    @GetMapping("/get")
    public ResponseEntity<?> getAll(){
        return ResponseEntity.status(200).body(chatService.getAll());
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody @Valid Chat chat){

        chatService.save(chat);
        return ResponseEntity.status(200).body(new ApiResponse("chat created successfully"));
    }

    @PutMapping("/close/{id}")
    public ResponseEntity<?> close(@PathVariable Integer id, Authentication auth){
        chatService.closeChat(id,  auth.getName());
        return ResponseEntity.status(200).body(new ApiResponse("chat closed successfully"));
    }

    @PostMapping("/open/{review_id}")
    public ResponseEntity<?> open(@PathVariable Integer review_id, Authentication auth){
        chatService.openChat(review_id, auth.getName());
        return ResponseEntity.status(200).body(new ApiResponse("chat opened successfully"));

    }

}
