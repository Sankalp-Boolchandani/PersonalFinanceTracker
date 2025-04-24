package com.self.pft.controller;

import com.self.pft.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/google")
    public ResponseEntity<?> handleGoogleCallback(@RequestParam String code){
        return authService.handleGoogleCallback(code);
    }

}
