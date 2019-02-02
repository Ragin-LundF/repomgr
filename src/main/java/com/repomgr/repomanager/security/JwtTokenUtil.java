package com.repomgr.repomanager.security;

import com.repomgr.repomanager.config.RepoManagerProperties;
import com.repomgr.repomanager.infrastructure.UserService;
import com.repomgr.repomanager.rest.model.UserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtTokenUtil {
    private final RepoManagerProperties repoManagerProperties;
    private final UserService userService;

    @Autowired
    public JwtTokenUtil(RepoManagerProperties repoManagerProperties, UserService userService) {
        this.repoManagerProperties = repoManagerProperties;
        this.userService = userService;
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = lookupClaim(token, Claims::getSubject);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = lookupClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    public String generateToken(UserDto user) {
        Claims claims = Jwts.claims().setSubject(user.getUsername());
        claims.put("scopes", Collections.singletonList(userService.lookupAuthority(user.getRole())));

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer("RepoManager")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + repoManagerProperties.getSecurity().getTokenExpirationTime() * 1000))
                .signWith(SignatureAlgorithm.HS256, repoManagerProperties.getSecurity().getSigningKey())
                .compact();
    }

    public <T> T lookupClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser()
                .setSigningKey(repoManagerProperties.getSecurity().getSigningKey())
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }
}