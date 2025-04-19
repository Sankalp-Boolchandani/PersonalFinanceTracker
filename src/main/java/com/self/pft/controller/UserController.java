package com.self.pft.controller;


import com.self.pft.entity.User;
import com.self.pft.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(path = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody User user){
        User createdUser = userService.createUser(user);
        if (createdUser!=null){
            return new ResponseEntity<>("User Created!!!!", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id){
        User userById = userService.findUserById(id);
        if (userById != null){
            return new ResponseEntity<>(userById, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<User> setUserBudget(@Valid
            @PathVariable Long id,
            @RequestParam @Positive BigDecimal budget)
            {
        return userService.setUserBudget(id, budget);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        return userService.loginUser(user);
    }

}
