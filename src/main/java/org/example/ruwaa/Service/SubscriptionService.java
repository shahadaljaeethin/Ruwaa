package org.example.ruwaa.Service;

import lombok.RequiredArgsConstructor;
import org.example.ruwaa.Api.ApiException;
import org.example.ruwaa.Model.Customer;
import org.example.ruwaa.Model.Subscription;
import org.example.ruwaa.Repository.CustomerRepository;
import org.example.ruwaa.Repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SubscriptionService
{
    private final SubscriptionRepository subscriptionRepository;
    private final CustomerRepository customerRepository;
    private final PaymentService paymentService;

    public void subscribe(String username){
        Customer c = customerRepository.findCustomerByUsername(username).orElseThrow(() -> new ApiException("user not found"));
        paymentService.processPayment(15.0,c.getUsers().getCards().get(0));
        if(c.getSubscription() != null){
            System.out.println(c.getSubscription().getEnd_date());
            c.getSubscription().setEnd_date(c.getSubscription().getEnd_date().plusMonths(1));
            subscriptionRepository.save(c.getSubscription());
            return;
        }
        Subscription sub = new Subscription();
        sub.setCustomer(c);
        sub.setSubscription_date(LocalDateTime.now());
        sub.setEnd_date(LocalDateTime.now().plusMonths(1));
        subscriptionRepository.save(sub);
    }

    public void subscribeThreeMonths(String username){
        Customer c = customerRepository.findCustomerByUsername(username).orElseThrow(() -> new ApiException("user not found"));
       if(c.getUsers().getCards().isEmpty()||c.getUsers().getCards()==null) throw new ApiException("add card first");
        paymentService.processPayment((15.0*3)-5,c.getUsers().getCards().get(0));
        if(c.getSubscription() != null){
            c.getSubscription().setEnd_date(c.getSubscription().getEnd_date().plusMonths(3));
            subscriptionRepository.save(c.getSubscription());
            return;
        }
        Subscription sub = new Subscription();
        sub.setCustomer(c);
        sub.setSubscription_date(LocalDateTime.now());
        sub.setEnd_date(LocalDateTime.now().plusMonths(3));
        subscriptionRepository.save(sub);
    }

    public void subscribeSixMonths(String username){
        Customer c = customerRepository.findCustomerByUsername(username).orElseThrow(() -> new ApiException("user not found"));
        if(c.getUsers().getCards().isEmpty()||c.getUsers().getCards()==null) throw new ApiException("add card first");

        paymentService.processPayment((15.0*6)-10,c.getUsers().getCards().get(0));
        if(c.getSubscription() != null){
            c.getSubscription().setEnd_date(c.getSubscription().getEnd_date().plusMonths(6));
            subscriptionRepository.save(c.getSubscription());
            return;
        }
        Subscription sub = new Subscription();
        sub.setCustomer(c);
        sub.setSubscription_date(LocalDateTime.now());
        sub.setEnd_date(LocalDateTime.now().plusMonths(6));
        subscriptionRepository.save(sub);
    }

    public void giftSubscribe(String username,String sender){
        Customer gifer = customerRepository.findCustomerByUsername(sender).orElseThrow(() -> new ApiException("user not found"));
        if(gifer.getUsers().getCards() == null){
            throw new ApiException("you dont have cards");
        }
        Customer c = customerRepository.findCustomerByUsername(username).orElseThrow(() -> new ApiException("user not found"));
        if (gifer == c){
            throw new ApiException("you cant gift subscription to your self");
        }
        paymentService.processPayment(15.0,gifer.getUsers().getCards().get(0));
        if(c.getSubscription() != null){
            c.getSubscription().setEnd_date(c.getSubscription().getEnd_date().plusMonths(1));
            subscriptionRepository.save(c.getSubscription());
            return;
        }
        Subscription sub = new Subscription();
        sub.setCustomer(c);
        sub.setSubscription_date(LocalDateTime.now());
        sub.setSubscription_date(LocalDateTime.now().plusMonths(1));
        subscriptionRepository.save(sub);
    }

    public void giftThreeSubscribe(String username,String sender){
        Customer gifer = customerRepository.findCustomerByUsername(sender).orElseThrow(() -> new ApiException("user not found"));
        if(gifer.getUsers().getCards() == null){
            throw new ApiException("you dont have cards");
        }
        Customer c = customerRepository.findCustomerByUsername(username).orElseThrow(() -> new ApiException("user not found"));
        if (gifer == c){
            throw new ApiException("you cant gift subscription to your self");
        }
        paymentService.processPayment((15.0*3)-5,gifer.getUsers().getCards().get(0));
        if(c.getSubscription() != null){
            c.getSubscription().setEnd_date(c.getSubscription().getEnd_date().plusMonths(3));
            subscriptionRepository.save(c.getSubscription());
            return;
        }
        Subscription sub = new Subscription();
        sub.setCustomer(c);
        sub.setSubscription_date(LocalDateTime.now());
        sub.setSubscription_date(LocalDateTime.now().plusMonths(3));
        subscriptionRepository.save(sub);
    }

    public void giftSixSubscribe(String username,String sender){
        Customer gifer = customerRepository.findCustomerByUsername(sender).orElseThrow(() -> new ApiException("user not found"));
        if(gifer.getUsers().getCards() == null){
            throw new ApiException("you dont have cards");
        }
        Customer c = customerRepository.findCustomerByUsername(username).orElseThrow(() -> new ApiException("user not found"));
        if (gifer == c){
            throw new ApiException("you cant gift subscription to your self");
        }
        paymentService.processPayment((15.0*6)-10,gifer.getUsers().getCards().get(0));
        if(c.getSubscription() != null){
            c.getSubscription().setEnd_date(c.getSubscription().getEnd_date().plusMonths(6));
            subscriptionRepository.save(c.getSubscription());
            return;
        }
        Subscription sub = new Subscription();
        sub.setCustomer(c);
        sub.setSubscription_date(LocalDateTime.now());
        sub.setSubscription_date(LocalDateTime.now().plusMonths(6));
        subscriptionRepository.save(sub);
    }

    public Subscription getSubscription(String username){
        Customer c = customerRepository.findCustomerByUsername(username).orElseThrow(() -> new ApiException("user not found"));
        if(c.getSubscription() == null){
            throw new ApiException("you have not subscribed yet");
        }
        return c.getSubscription();
    }
}
