package com.repomgr.repomanager.filter;

import com.repomgr.repomanager.config.RepoManagerProperties;
import com.repomgr.repomanager.config.WebSecurityConfig;
import com.repomgr.repomanager.constants.Constants;
import com.repomgr.repomanager.security.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
import java.util.Collections;

/**
 * Filter for JWT token management.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final static String TOKEN_PREFIX = "Bearer";

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
     * @param request       HttpRequests
     * @param response      HttpResponse
     * @param chain         FilterChain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (! isWhitelistUri(request.getRequestURI())) {
            String header = request.getHeader(repoManagerProperties.getSecurity().getHeaderName());
            String username = null;
            String token = null;

            if (header != null && header.startsWith(TOKEN_PREFIX)) {
                token = header.replace(TOKEN_PREFIX, "").trim();

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
                logger.error("Can not find token. Please add a Bearer token with the prefix [" + TOKEN_PREFIX + "].");
            }

            if (!StringUtils.isEmpty(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtTokenUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.debug("User [" + username + "] logged in.");
                }
            }
        }

        chain.doFilter(request, response);
    }

    /**
     * Check, if the given URI is part of the whitelist.
     *
     * @see Constants#NO_AUTH_URLS
     */
    private boolean isWhitelistUri(String uri) {
        for (String whitelistUri : Constants.NO_AUTH_URLS) {
            if (uri.contains(whitelistUri)) {
                return true;
            }
        }
        return false;
    }
}
