package com.mylearning.springsecurityjwt.config;

import com.mylearning.springsecurityjwt.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//Once per request filter is something which comes in between for each request made
@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            final String jwt = authHeader.substring(7);
            final String username = jwtService.extractUsername(jwt);

            // SecurityContext in Spring Security is thread-local, meaning it stores authentication details only for the currently authenticated user in the current request thread.
            // SecurityContextHolder holds the security context for the current thread,
            // and this context contains the Authentication object.
            // The Authentication object tells you whether a user is authenticated and holds details like their username, roles, and authorities.
            //If a valid authentication already exists in the context, you don’t want to authenticate the user again. Otherwise, you might overwrite valid authentication data or cause conflicts.
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtService.isTokenValid(jwt, userDetails)) {

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    //Adding the session id and its ip address
                    WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
                    System.out.println("Remote IP address: " + details.getRemoteAddress());
                    System.out.println("Session ID: " + details.getSessionId());
                    authToken.setDetails(details);

                    //Setting the securityContext to store it in its local thread memory
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            //once added in SecurityContext will call next filter
            filterChain.doFilter(request, response);
        }
    }
}
//Please check which details for better understanding of all the concepts and the
//Entire flow
//A request comes in with a JWT token.
//You extract the token, validate it, and get the username and roles.
//You check if the security context already has an authenticated user.
//If not, you authenticate and set the context — otherwise, you skip re-authentication.

//Key Points:
//=>Per-Request, Per-User: The SecurityContext is tied to a single request and holds the authentication information for the current user making that request.
//=>Thread Local Storage: Spring Security uses a ThreadLocal to store the context, which means each thread has its own isolated copy of the security context.
//=>No Cross-User Data: One user's authentication details are never mixed with another user's session, even if they hit the same endpoint.

//  Example Flow:
//  User A logs in:
//JWT is validated for User A.
//SecurityContextHolder.getContext().setAuthentication(authenticationForUserA)

//User B logs in (separate request):
//JWT is validated for User B.
//SecurityContextHolder.getContext().setAuthentication(authenticationForUserB)

//Thread Isolation:
//In User A’s request thread, SecurityContextHolder holds User A’s authentication.
//In User B’s request thread, SecurityContextHolder holds User B’s authentication.
//They don't interfere with each other.

//Constructor 1
//public UsernamePasswordAuthenticationToken(Object principal, Object credentials)
//  UsernamePasswordAuthenticationToken token =
//      new UsernamePasswordAuthenticationToken("john_doe", "password123");

//Constructor 2
//public UsernamePasswordAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities)
// List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
// UsernamePasswordAuthenticationToken authenticatedToken =
//     new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

//principal: The authenticated user object (usually an instance of UserDetails).
//credentials: Typically null after authentication (since we don’t need to store the password).
//authorities: A collection of the roles/authorities granted to the user.