package com.self.pft.service;

import com.self.pft.constants.SecurityConstants;
import com.self.pft.entity.User;
import com.self.pft.repository.UserRepository;
import com.self.pft.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class AuthService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public ResponseEntity<?> handleGoogleCallback(String code){
        try{
            String tokenEndpoint = "https://oauth2.googleapis.com/token";

            MultiValueMap<String, String> params=new LinkedMultiValueMap<>();
            params.add("code", code);
            params.add("client_id", SecurityConstants.GOOGLE_AUTH_CLIENT_ID);
            params.add("client_secret", SecurityConstants.GOOGLE_AUTH_CLIENT_SECRET);
            params.add("grant_type", "authorization_code");
            params.add("redirect_uri", "https://developers.google.com/oauthplayground");

            HttpHeaders headers=new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request=new HttpEntity<>(params, headers);

            ResponseEntity<Map> mapResponseEntity = restTemplate.postForEntity(tokenEndpoint, request, Map.class);
            String idToken = mapResponseEntity.getBody().get("id_token").toString();
            String userInfoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token="+idToken;
            ResponseEntity<Map> entity = restTemplate.getForEntity(userInfoUrl, Map.class);
            if (entity.getStatusCode().equals(HttpStatus.OK)){
                Map body = entity.getBody();
                String email=body.get("email").toString();
                try {
                    userDetailsService.loadUserByUsername(email);
                } catch (UsernameNotFoundException e) {
                    User user=new User();
                    user.setName("user");
                    user.setUsername(email);
                    user.setEmail(email);
                    user.setRoles(List.of("USER"));
                    user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                    userRepository.save(user);
                }
                String jwt = jwtUtil.generateToken(email);
                return ResponseEntity.ok(Collections.singletonMap("jwt", jwt));
            }
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("Error while handling google callback", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
