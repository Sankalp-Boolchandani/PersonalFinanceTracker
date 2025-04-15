package com.self.pft.service;

import com.self.pft.entity.User;
import com.self.pft.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User createUser(User user){
        return userRepository.save(user);
    }

    public User findUserById(Long id){
        return userRepository.findById(id).get();
    }

    public ResponseEntity<User> setUserBudget(Long id, BigDecimal budget) {
        try{
            User user = findUserById(id);
            user.setBudgetLimit(budget);
            userRepository.save(user);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
