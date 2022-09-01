package oasip.us1.backend.controllers;

import oasip.us1.backend.dtos.UserMatchingPasswordDto;
import oasip.us1.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/api/match")
public class MatchController {
    @Autowired
    private UserService userService;

    @PostMapping("")
    public ResponseEntity matchPassword(@Valid @RequestBody UserMatchingPasswordDto user, BindingResult bindingResult, WebRequest request){
        return userService.matchingPassword(user, bindingResult, request);
    }
}
