package org.example.ruwaa.Service;

import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.example.ruwaa.Api.ApiException;
import org.example.ruwaa.Model.*;
import org.example.ruwaa.Repository.ChatRepository;
import org.example.ruwaa.Repository.MessageRepository;
import org.example.ruwaa.Repository.ReviewRepository;
import org.example.ruwaa.Repository.UsersRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService
{
    private final ChatRepository chatRepository;
    private final UsersRepository usersRepository;
    private final ReviewRepository reviewRepository;
    private final MessageRepository messageRepository;

    public List<Chat> getAll(){
        List<Chat> chats = chatRepository.findAll();
        if (chats.isEmpty()){
            throw new ApiException("there are no chats");
        }
        return chats;
    }

    public void save(Chat chat){
        chatRepository.save(chat);
    }

    public void closeChat(Integer chat_id,String username){
        Users u = usersRepository.findUserByUsername(username).orElseThrow(() -> new ApiException("user not found"));
        Chat c =  chatRepository.findById(chat_id).orElseThrow(() -> new ApiException("chat not found"));
        c.setIsOpen(false);
    }

    public void openChat(Integer review_id,String username){
        Users u = usersRepository.findUserByUsername(username).orElseThrow(() -> new ApiException("user not found"));
        Review r = reviewRepository.findReviewById(review_id).orElseThrow(() -> new ApiException("review not found"));
        Expert e = r.getExpert();
        if (u != r.getPost().getUsers()){
            throw new ApiException("this post doesnt belong to you");
        }
        if (chatRepository.findById(review_id).isPresent()){
            Chat c = chatRepository.findById(review_id).orElseThrow(() -> new ApiException("chat not found"));
            if (c.getIsOpen()){
                throw new ApiException("chat already open");
            }
            throw new ApiException("chat already ended");

        }
        //creating the chatbox
        List<Message> start = new ArrayList<>();


        //creating the chat and adding the box into it
        Chat chat = new Chat();
        chat.setIsOpen(true);
        chat.setMessages(start);
        chat.setReview(r);
        chatRepository.save(chat);

        //create the first message
        Message starter_message = new Message();
        starter_message.setUsers(e.getUsers());
        starter_message.setText(r.getContent());
        starter_message.setSent_at(LocalDateTime.now());
        starter_message.setChat(chat);
        messageRepository.save(starter_message);



    }
}
