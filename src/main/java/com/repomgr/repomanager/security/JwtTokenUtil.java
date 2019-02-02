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

/**
 * Utilitiy class for JWT token handling.
 * <br />
 * This class is responsible for all token related actions.
 */
@Component
public class JwtTokenUtil {
    private final RepoManagerProperties repoManagerProperties;
    private final UserService userService;

    @Autowired
    public JwtTokenUtil(RepoManagerProperties repoManagerProperties, UserService userService) {
        this.repoManagerProperties = repoManagerProperties;
        this.userService = userService;
    }

    /**
     * Validate the token.
     * <br />
     * The following checks will be done:
     * <ul>
     *     <li>Validate token (will be done by jjwt)</li>
     *     <li>Check if correct username claim exists</li>
     *     <li>Check if token is not expired</li>
     * </ul>
     *
     * @param token         JWT Token
     * @param userDetails   User Details to compare.
     * @return              true if token was valid
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = lookupClaim(token, Claims::getSubject);
        return (username.equals(userDetails.getUsername()) && ! isTokenExpired(token));
    }

    /**
     * Check if token is expired.
     *
     * @param token     JWT token
     * @return          true if token is expired
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = lookupClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    /**
     * Generate a new JWT token.
     * <br />
     * The token contains the user as a subject and the role as a scope.
     *
     * @param user      User informations
     * @return          JWT token as string
     */
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

    /**
     * Reads the claims of the token.
     * <br />
     * To read a subject for example you can use the following expression:
     * <br />
     * lookupClaim(token, Claims::getSubject);
     *
     * @param token             JWT token
     * @param claimsResolver    Function of Claims object, which will be executed
     * @param <T>               Resolver class
     * @return                  Content of the claim
     */
    public <T> T lookupClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser()
                .setSigningKey(repoManagerProperties.getSecurity().getSigningKey())
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }
}