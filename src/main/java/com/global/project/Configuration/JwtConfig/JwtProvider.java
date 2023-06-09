package com.global.project.Configuration.JwtConfig;

import com.global.project.Entity.UserEntity;
import com.global.project.Repository.UserRepository;
import com.global.project.Configuration.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtProvider {
    @Value("${jwt.SECRET_KEY}")
    private String JWT_SECRET;
    @Value("${jwt.JWT_EXPIRATION}")
    private int JWT_EXPIRATION;
    @Autowired
    UserRepository userRepository;

    public String generateToken(UserDetailsImpl customDetailService){
        return this.generateTokenByUsername(customDetailService.getUsername());
    }
    public String generateTokenByUsername(String Username){
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);
        UserEntity userEntity = userRepository.findByUsername(Username).get();
        return Jwts.builder()
                .setSubject(Long.toString(userEntity.getId()))
                .claim("username", userEntity.getUsername())
                .claim("role", userEntity.getRole().getName())
                .setExpiration(expiryDate)
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();
    }

    public Long getUserIdFromJWT(String token){
        Claims claims = Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }

    public String getKeyByValueFromJWT(String key, String token){
        Claims claims = Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();
        return claims.get(key,String.class);
    }
}
