package br.com.nszandrew.roadmap.controller;

import br.com.nszandrew.roadmap.model.dto.RegisterRequestDTO;
import br.com.nszandrew.roadmap.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api")
public class UserController {


    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequestDTO data, UriComponentsBuilder uriBuilder) {
        String user = userService.register(data);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @GetMapping("/verify-account")
    public ResponseEntity<String> verifyEmail(@RequestParam String code) {
        userService.verifyEmail(code);
        return new ResponseEntity<>("Email verificado com sucesso!", HttpStatus.OK);
    }
}
