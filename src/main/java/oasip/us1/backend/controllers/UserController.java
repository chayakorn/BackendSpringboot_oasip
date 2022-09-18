package oasip.us1.backend.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import oasip.us1.backend.dtos.UserPostDto;
import oasip.us1.backend.dtos.UserPutDto;
import oasip.us1.backend.entities.User;
import oasip.us1.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@CrossOrigin
@RestController
@Slf4j
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("")
    private ResponseEntity getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    private ResponseEntity getUserById(@PathVariable Integer id, WebRequest request) {
        return userService.getUserById(id, request);
    }

    @PostMapping("")
    private ResponseEntity createUser(@Valid @RequestBody UserPostDto newUser, BindingResult bindingResult, WebRequest request) {
        return userService.save(newUser, bindingResult, request);
    }

    @DeleteMapping("/{id}")
    private ResponseEntity deleteUser(@PathVariable Integer id, WebRequest request) {
        return userService.delete(id, request);
    }

    @PutMapping("/{id}")
    private ResponseEntity updateUser(@Valid @PathVariable Integer id, @RequestBody UserPutDto updateUser, BindingResult bindingResult, WebRequest request) {
        return userService.update(id, updateUser, bindingResult, request);
    }
    @GetMapping("/refreshtoken")
    private void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if(authorizationHeader != null && authorizationHeader.startsWith("US1 ")){
            try {
                String refresh_token = authorizationHeader.substring("US1 ".length());
                Algorithm algorithm = Algorithm.HMAC256("us1t2pkey".getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refresh_token);
                String email = decodedJWT.getSubject();
                User user = userService.getUser(email);
                ArrayList<String> roles = new ArrayList<String>();
                roles.add(user.getRole().toString());
                String access_token = JWT.create()
                        .withSubject(user.getEmail())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 10*60*1000))
                        .withIssuer(request.getRequestURI().toString())
                        .withClaim("roles",roles)
                        .sign(algorithm);
                Map<String,String> tokens = new HashMap<>();
                tokens.put("access_token",access_token);
                tokens.put("refresh_token",refresh_token);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(),tokens);
            }catch(Exception exception){
                log.error("Error loggin in:{}",exception.getMessage());
                response.setHeader("error", exception.getMessage());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                Map<String,String> error = new HashMap<>();
                error.put("error",exception.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(),error);
            }
        }else {
            throw new RuntimeException("Refresh token is missing");
        }
    }
}
