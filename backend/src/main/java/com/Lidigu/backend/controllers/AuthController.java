package com.Lidigu.backend.controllers;

import com.Lidigu.backend.controllers.config.UserAuthProvider;
import com.Lidigu.backend.dtos.CredentialDto;
import com.Lidigu.backend.dtos.SignUpDto;
import com.Lidigu.backend.dtos.UserDto;
import com.Lidigu.backend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final UserAuthProvider userAuthProvider;
    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody CredentialDto credentialsDto){
        UserDto user = userService.login(credentialsDto);
        user.setToken(userAuthProvider.createToken(user));
        return ResponseEntity.ok(user);
    }
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody SignUpDto signUpDto){
      UserDto user =  userService.register(signUpDto);
        user.setToken(userAuthProvider.createToken(user));
      return ResponseEntity.created(URI.create("/users/"+user.getId())).body(user);
    }
}
