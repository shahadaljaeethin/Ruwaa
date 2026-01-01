package org.example.ruwaa.Service;

import lombok.RequiredArgsConstructor;
import org.example.ruwaa.Api.ApiException;
import org.example.ruwaa.Model.Customer;
import org.example.ruwaa.Model.Users;
import org.example.ruwaa.Repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService
{
    private final CustomerRepository customerRepository;

    public List<Customer> findAll(){
        List<Customer> customers = customerRepository.findAll();
        if (customers.isEmpty()){
            throw new ApiException("there are no customers yet");
        }
        return customers;
    }

    public void update(Integer id, Users user){
        Customer c = customerRepository.findCustomerById(id).orElseThrow(()-> new ApiException("customer not found"));
        Users u =  c.getUsers();
        u.setUsername(user.getUsername());
        u.setPassword(user.getPassword());
        u.setEmail(user.getEmail());
        u.setPhone(user.getPhone());
        u.setName(user.getName());
        c.setUsers(u);
        customerRepository.save(c);
    }

    public void delete(Integer id){
        Customer c = customerRepository.findCustomerById(id).orElseThrow(() -> new ApiException("customer not found"));
        customerRepository.delete(c);
    }
}
