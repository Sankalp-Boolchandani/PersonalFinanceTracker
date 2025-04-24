package com.self.pft.service;

import com.self.pft.entity.User;
import com.self.pft.repository.UserRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class UserServiceTests {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Disabled
    @Test
    void setUserNames(){
        List<User> allUsers = userService.getAllUsers();
        for (User user: allUsers){
            if (user.getUsername().isEmpty()){
                user.setUsername(user.getName() + "1");
                userService.saveUser(user);
            }
        }
    }

    @Test
    void setUserRoles(){
        List<User> allUsers = userService.getAllUsers();
        for (User user: allUsers){
            user.setRoles(List.of("USER"));
            userService.saveUser(user);
        }
    }

    @Test
    void deleteUsers(){
        List<User> allUsers = userService.getAllUsers();
        allUsers.forEach(x-> {
            if (x.getRoles() == null){
                userRepository.delete(x);
            }
        });
    }

}
