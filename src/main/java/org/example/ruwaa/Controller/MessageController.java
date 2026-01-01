package org.example.ruwaa.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ruwaa.Api.ApiResponse;
import org.example.ruwaa.Model.Message;
import org.example.ruwaa.Service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/message")
@RequiredArgsConstructor
public class MessageController
{
    private final MessageService messageService;

    @PostMapping("/send/{chat_id}")
    public ResponseEntity<?> sendMessage(@PathVariable Integer chat_id, @RequestBody @Valid Message message, Authentication auth){
        messageService.send(auth.getName(), chat_id,message);
        return ResponseEntity.status(200).body(new ApiResponse("message sent successfully"));
    }

    @GetMapping("/display-chat/{chat_id}")
    public ResponseEntity<?> displayChat(@PathVariable Integer chat_id, Authentication auth){
        return ResponseEntity.status(200).body(messageService.displayChat(auth.getName(), chat_id));
    }

    @PutMapping("/edit-message/{message_id}")
    public ResponseEntity<?> editMessage(Authentication auth,@PathVariable Integer message_id, @RequestBody String message){
        messageService.update(auth.getName(),message_id,message);
        return ResponseEntity.status(200).body(new ApiResponse("message edited successfully"));
    }

    @DeleteMapping("/delete-message/{message_id}")
    public ResponseEntity<?> deleteMessage(@PathVariable Integer message_id, Authentication auth){
        messageService.delete(auth.getName(),message_id);
        return ResponseEntity.status(200).body(new ApiResponse("message deleted successfully"));
    }
}
