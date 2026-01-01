package org.example.ruwaa.Service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@AllArgsConstructor
public class SendMailService {

    @Autowired
    private JavaMailSender javaEmailSender;

    public void sendMessage(String email, String subject, String body) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body);
        javaEmailSender.send(simpleMailMessage);
    }

//    public void testMail(){
//        System.out.println("testtt mail");
//        sendMessage("shahadkn67@outlook.com","Hi 1","many hellos 4");
//        sendMessage("shahadaljaeethin@gmail.com","Hi 2","many hellos 4");
//
//    }

}
