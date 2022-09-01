package oasip.us1.backend.controllers;

import oasip.us1.backend.dtos.UserPostDto;
import oasip.us1.backend.dtos.UserPutDto;
import oasip.us1.backend.entities.User;
import oasip.us1.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.validation.Valid;

@CrossOrigin
@RestController
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
}
