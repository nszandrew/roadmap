package br.com.nszandrew.roadmap.controller;

import br.com.nszandrew.roadmap.model.dto.user.RegisterRequestDTO;
import br.com.nszandrew.roadmap.model.dto.user.UserDetailsDTO;
import br.com.nszandrew.roadmap.model.user.Role;
import br.com.nszandrew.roadmap.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AdminController {


    private final AdminService adminService;

    public AdminController(AdminService userService) {
        this.adminService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequestDTO data, UriComponentsBuilder uriBuilder) {
        String user = adminService.register(data);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @GetMapping("/verify-account")
    public ResponseEntity<String> verifyEmail(@RequestParam String code) {
        adminService.verifyEmail(code);
        return new ResponseEntity<>("Email verificado com sucesso!", HttpStatus.OK);
    }

    @GetMapping("/getallusers")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<UserDetailsDTO>> getAllUsers() {
        return new ResponseEntity<>(adminService.getAllUsers(), HttpStatus.OK);
    }

    @PatchMapping("/add-role")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> addRole(@RequestParam Long id, @RequestParam Role role) {
        adminService.changeRole(id, role);
        return new ResponseEntity<>("Cargo alterado com sucesso", HttpStatus.OK);
    }

    @PatchMapping("/remove-role")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> removeRole(@RequestParam Long id, @RequestParam Role role) {
        adminService.removeRole(id, role);
        return new ResponseEntity<>("Cargo removido com sucesso", HttpStatus.OK);
    }
}
