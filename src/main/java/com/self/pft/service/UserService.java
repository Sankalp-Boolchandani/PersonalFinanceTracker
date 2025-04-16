package com.self.pft.service;

import com.self.pft.entity.User;
import com.self.pft.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private static final PasswordEncoder passwordEncoder=new BCryptPasswordEncoder();

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User createUser(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // this is to be used at places so that we dont have to use the above method as it encodes the password
    // and if we use the above method after operations, it would encode an already encoded value
    private User saveUser(User user){
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
