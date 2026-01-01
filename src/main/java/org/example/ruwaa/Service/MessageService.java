package org.example.ruwaa.Service;

import lombok.RequiredArgsConstructor;
import org.example.ruwaa.Api.ApiException;
import org.example.ruwaa.DTOs.ChatBoxDTO;
import org.example.ruwaa.Model.Chat;
import org.example.ruwaa.Model.Message;
import org.example.ruwaa.Model.Users;
import org.example.ruwaa.Repository.ChatRepository;
import org.example.ruwaa.Repository.MessageRepository;
import org.example.ruwaa.Repository.UsersRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService
{
    private final MessageRepository messageRepository;
    private final UsersRepository usersRepository;
    private final ChatRepository chatRepository;

    public List<Message> getAll(){
        List<Message> messages = messageRepository.findAll();
        if (messages.isEmpty()){
            throw new ApiException(" no messages found");
        }
        return messages;
    }

    public void send(String username,Integer chat_id, Message m){
        Users u = usersRepository.findUserByUsername(username).orElseThrow(() -> new ApiException("user not found"));
        Chat c = chatRepository.findChatById(chat_id).orElseThrow(() -> new ApiException("chat not found"));
        if (u != c.getReview().getPost().getUsers() && u != c.getReview().getExpert().getUsers()){
            throw new ApiException("you are not allowed to send this message");
        }
        if (!c.getIsOpen()){
            throw new ApiException("chat is closed");
        }

        m.setSent_at(LocalDateTime.now());
        m.setUsers(u);
        m.setChat(c);
        messageRepository.save(m);
    }

    public void update(String username, Integer id, String text){
        Users u = usersRepository.findUserByUsername(username).orElseThrow(() -> new ApiException("user not found"));
        Message m = messageRepository.findMessageById(id).orElseThrow(() -> new ApiException("message not found"));
        if (u != m.getUsers()){
            throw new ApiException("you are not allowed to edit this message");
        }

        m.setText(text);
        messageRepository.save(m);
    }

    public void delete(String username,Integer id){
        Users u = usersRepository.findUserByUsername(username).orElseThrow(() -> new ApiException("user not found"));
        Message m = messageRepository.findMessageById(id).orElseThrow(() -> new ApiException("message not found"));
        if (u != m.getUsers()){
            throw new ApiException("you are not allowed to delete this message");
        }
        messageRepository.delete(m);
    }

    public List<ChatBoxDTO> displayChat(String username, Integer chat_id){
        Users u = usersRepository.findUserByUsername(username).orElseThrow(() -> new ApiException("user not found"));
        Chat c = chatRepository.findChatById(chat_id).orElseThrow(() -> new ApiException("chat not found"));
        if (u != c.getReview().getPost().getUsers() && u != c.getReview().getExpert().getUsers()){
            throw new ApiException("you are not allowed to see this chat");
        }
        List<Message> messages = messageRepository.findAllMessagesByChatId(chat_id).orElseThrow(() -> new ApiException("chat not found"));
        System.out.println(messages.get(0).getUsers());
        List<ChatBoxDTO> chat= new ArrayList<>();
        for (Message m : messages){
            ChatBoxDTO dto = new ChatBoxDTO();
            dto.setSender(usersRepository.findUserByMessageId(m.getId()).getName());
            dto.setText(m.getText());
            dto.setDate(m.getSent_at());
            chat.add(dto);
        }
        return chat;


    }
}
