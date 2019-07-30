package com.repomgr.repomanager.filter;

import com.repomgr.repomanager.config.RepoManagerProperties;
import com.repomgr.repomanager.constants.Constants;
import com.repomgr.repomanager.security.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter for JWT token management.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String TOKEN_PREFIX = "Bearer";

    @Autowired
    @Qualifier("userService")
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private RepoManagerProperties repoManagerProperties;

    /**
     * Filter method for handle token management.
     *
     * @param request  HttpRequests
     * @param response HttpResponse
     * @param chain    FilterChain
     * @throws IOException      io exception
     * @throws ServletException servlet exception
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        logger.debug("[JwtAuthenticationFilter][doFilterInternal] Filter execution started");
        if (!isWhitelistUri(request.getRequestURI())) {
            String header = request.getHeader(repoManagerProperties.getSecurity().getHeaderName());
            String token = readTokenFromHeader(header);
            String username = readUsernameFromToken(token);

            if (!StringUtils.isEmpty(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtTokenUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.debug("User [" + username + "] logged in.");
                }
            }
        }

        chain.doFilter(request, response);
        logger.debug("[JwtAuthenticationFilter][doFilterInternal] Filter execution finished");
    }

    /**
     * read the token from header
     *
     * @param header    Header
     * @return          Token only or null
     */
    private String readTokenFromHeader(String header) {
        logger.debug("[JwtAuthenticationFilter][readTokenFromHeader] Read token from header started.");
        String token = null;
        if (StringUtils.startsWithIgnoreCase(header, TOKEN_PREFIX)) {
            token = header.replace(TOKEN_PREFIX, "").trim();
        } else {
            logger.error("Can not find token. Please add a Bearer token with the prefix [" + TOKEN_PREFIX + "].");
        }

        logger.debug("[JwtAuthenticationFilter][readTokenFromHeader] Read token from header finished.");
        return token;
    }

    /**
     * Read username from token, if token exists.
     *
     * If there is no token or the username could not be extracted, then this method returns null.
     *
     * @param token     Token
     * @return          Username or null
     */
    private String readUsernameFromToken(String token) {
        logger.debug("[JwtAuthenticationFilter][readUsernameFromToken] Read username from token started.");
        String username = null;
        if (! StringUtils.isEmpty(token)) {
            try {
                username = jwtTokenUtil.lookupClaim(token, Claims::getSubject);
            } catch (IllegalArgumentException e) {
                logger.error("Can not read username from token.", e);
            } catch (ExpiredJwtException e) {
                logger.warn("The token is expired.", e);
            } catch (SignatureException e) {
                logger.error("Authentication failed.");
            }
        } else {
            logger.error("No token found.");
        }

        logger.debug("[JwtAuthenticationFilter][readUsernameFromToken] Read username from token finished.");
        return username;
    }

    /**
     * Check, if the given URI is part of the whitelist.
     *
     * @see Constants#NO_AUTH_URLS
     */
    private boolean isWhitelistUri(String uri) {
        logger.debug("[JwtAuthenticationFilter][isWhitelistUri] Whitelist check started.");
        boolean whitelist = false;
        for (String whitelistUri : Constants.NO_AUTH_URLS) {
            if (uri.contains(whitelistUri)) {
                whitelist = true;
                break;
            }
        }
        logger.debug("[JwtAuthenticationFilter][isWhitelistUri] Whitelist check finished.");
        return whitelist;
    }
}
