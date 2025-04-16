package com.self.pft.service;

import com.self.pft.entity.User;
import com.self.pft.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByName(username);
        if (user!=null){
            org.springframework.security.core.userdetails.User.builder()
                    .username(user.getName())
                    .password(user.getPassword())
                    .build();
        }
        throw new UsernameNotFoundException("user "+username+" not found!");
    }
}
