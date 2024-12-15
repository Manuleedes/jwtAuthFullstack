package com.Lidigu.backend.services;

import com.Lidigu.backend.dtos.CredentialDto;
import com.Lidigu.backend.dtos.SignUpDto;
import com.Lidigu.backend.dtos.UserDto;
import com.Lidigu.backend.entities.User;
import com.Lidigu.backend.exceptions.AppException;
import com.Lidigu.backend.mappers.UserMapper;
import com.Lidigu.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserDto login(CredentialDto credentialDto){
   User user=userRepository.findByLogin(credentialDto.login())
                .orElseThrow(()-> new AppException("Unknown User", HttpStatus.NOT_FOUND));
   if(passwordEncoder.matches(CharBuffer.wrap(credentialDto.password()),
           user.getPassword())){
       return userMapper.toUserDto(user);
   }
   throw new AppException("Invalid Password",HttpStatus.BAD_REQUEST);
    }
    public UserDto register(SignUpDto signUpDto){
        Optional<User> oUser=userRepository.findByLogin(signUpDto.login());
        if (oUser.isPresent()){
            throw new AppException("Login already exists",HttpStatus.BAD_REQUEST);
        }
        User user = userMapper.signUpToUser(signUpDto);

        user.setPassword(passwordEncoder.encode(CharBuffer.wrap(signUpDto.password())));
        User savedUser = userRepository.save(user);
        return userMapper.toUserDto(savedUser);
    }
}
