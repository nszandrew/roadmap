package br.com.nszandrew.roadmap.controller;

import br.com.nszandrew.roadmap.model.dto.RegisterRequestDTO;
import br.com.nszandrew.roadmap.model.user.Role;
import br.com.nszandrew.roadmap.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PatchMapping("/add-role")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> addRole(@RequestParam Long id, @RequestParam Role role) {
        userService.changeRole(id, role);
        return new ResponseEntity<>("Cargo alterado com sucesso", HttpStatus.OK);
    }

    @PatchMapping("/remove-role")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> removeRole(@RequestParam Long id, @RequestParam Role role) {
        userService.removeRole(id, role);
        return new ResponseEntity<>("Cargo removido com sucesso", HttpStatus.OK);
    }
}
