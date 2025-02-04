package br.com.nszandrew.roadmap.controller;

import br.com.nszandrew.roadmap.model.dto.user.UserDetailsDTO;
import br.com.nszandrew.roadmap.model.dto.user.UserInfoResponse;
import br.com.nszandrew.roadmap.model.dto.user.UserUpdateDTO;
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

    @PutMapping("/user/edit")
    public ResponseEntity<String> editUser(@RequestBody UserUpdateDTO data){
        return new ResponseEntity<>(userService.editUser(data), HttpStatus.OK);
    }

    @GetMapping("/user/info")
    public ResponseEntity<UserInfoResponse> getUserInfo(){
        return new ResponseEntity<>(userService.getUserInfo(), HttpStatus.OK);
    }
}
