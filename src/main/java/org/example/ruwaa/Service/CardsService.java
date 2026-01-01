package org.example.ruwaa.Service;

import lombok.RequiredArgsConstructor;
import org.example.ruwaa.Api.ApiException;
import org.example.ruwaa.Model.Card;
import org.example.ruwaa.Model.Users;
import org.example.ruwaa.Repository.CardsRepository;
import org.example.ruwaa.Repository.UsersRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardsService
{
    private final CardsRepository cardsRepository;
    private final UsersRepository usersRepository;

    public void addCard(Card card,String username){
        Users u = usersRepository.findUserByUsername(username).orElseThrow(() -> new ApiException("user not found"));
        card.setUsers(u);
        cardsRepository.save(card);
    }

    public void deleteCard(Integer id, String username){
        Users u = usersRepository.findUserByUsername(username).orElseThrow(() -> new ApiException("user not found"));
        Card c = cardsRepository.findCardById(id).orElseThrow(() -> new ApiException("card not found"));

        if (c.getUsers() != u ){
            throw new ApiException("user does not belong to this card");
        }

        cardsRepository.delete(c);
    }
}
