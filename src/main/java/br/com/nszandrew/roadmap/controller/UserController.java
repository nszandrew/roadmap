package br.com.nszandrew.roadmap.controller;

import br.com.nszandrew.roadmap.model.dto.UserDetailsDTO;
import br.com.nszandrew.roadmap.model.dto.UserUpdateDTO;
import br.com.nszandrew.roadmap.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/me")
    public ResponseEntity<UserDetailsDTO> getUser(){
        return new ResponseEntity<>(userService.getUser(), HttpStatus.OK);
    }

    @PutMapping("/user/edit")
    public ResponseEntity<String> editUser(@RequestBody UserUpdateDTO data){
        return new ResponseEntity<>(userService.editUser(data), HttpStatus.OK);
    }
}
