package com.self.pft.controller;


import com.self.pft.entity.User;
import com.self.pft.entity.request.UserLoginRequest;
import com.self.pft.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "User APIs")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "get-all-users")
    @GetMapping
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }

    @Operation(summary = "create-new-user")
    @PostMapping("/signup")
    public ResponseEntity<String> createUser(@RequestBody User user){
        User createdUser = userService.createUser(user);
        if (createdUser!=null){
            return new ResponseEntity<>("User Created!!!!", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "get-user-by-id")
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id){
        User userById = userService.findUserById(id);
        if (userById != null){
            return new ResponseEntity<>(userById, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "set-user-budget")
    @PostMapping("/{id}")
    public ResponseEntity<User> setUserBudget(@Valid
            @PathVariable Long id,
            @RequestParam @Positive BigDecimal budget)
            {
        return userService.setUserBudget(id, budget);
    }

    @Operation(summary = "user-login")
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginRequest user) {
        return userService.loginUser(user);
    }

}
