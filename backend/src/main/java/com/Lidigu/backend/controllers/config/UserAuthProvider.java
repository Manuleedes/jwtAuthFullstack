package com.Lidigu.backend.controllers.config;

import com.Lidigu.backend.dtos.UserDto;
import com.Lidigu.backend.entities.User;
import com.Lidigu.backend.exceptions.AppException;
import com.Lidigu.backend.mappers.UserMapper;
import com.Lidigu.backend.repositories.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Collections;
import java.util.Date;

@RequiredArgsConstructor
@Component
public class UserAuthProvider {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;

    @PostConstruct
    protected void init(){
        secretKey= Base64.getEncoder().encodeToString(secretKey.getBytes());
    }
    public String createToken(UserDto dto){
        Date now = new Date();
        Date validity= new Date(now.getTime()+ 3_600_000);
        return JWT.create()
                .withIssuer(dto.getLogin())
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .withClaim("firstName",dto.getFirstName())
                .withClaim("lastName",dto.getLastName())
                .sign(Algorithm.HMAC256(secretKey));
    }
    public Authentication validateToken(String token){
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        JWTVerifier verifier= JWT.require(algorithm).build();

        DecodedJWT decoded= verifier.verify(token);

        UserDto user= UserDto.builder()
                .login(decoded.getIssuer())
                .firstName(decoded.getClaim("firstName").asString())
                .lastName(decoded.getClaim("lastName").asString())
                .build();

        return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
    }

    public Authentication validateTokenStrongly(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        JWTVerifier verifier= JWT.require(algorithm).build();

        DecodedJWT decoded= verifier.verify(token);
        User user = userRepository.findByLogin(decoded.getIssuer())
                .orElseThrow(()-> new AppException("Unknown user", HttpStatus.NOT_FOUND));

        return new UsernamePasswordAuthenticationToken(userMapper.toUserDto(user), null, Collections.emptyList());
    }
}
