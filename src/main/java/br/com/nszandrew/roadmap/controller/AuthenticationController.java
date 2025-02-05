package br.com.nszandrew.roadmap.controller;

import br.com.nszandrew.roadmap.model.dto.user.*;
import br.com.nszandrew.roadmap.model.user.User;
import br.com.nszandrew.roadmap.repository.user.UserRepository;
import br.com.nszandrew.roadmap.service.AdminService;
import br.com.nszandrew.roadmap.service.TokenService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api")
@Slf4j
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final AdminService adminService;

    public AuthenticationController(AuthenticationManager authenticationManager, TokenService tokenService, UserRepository userRepository, AdminService adminService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.adminService = adminService;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginDTO data) {
        try {
            var authToken = new UsernamePasswordAuthenticationToken(data.email(), data.password());
            var authentication = authenticationManager.authenticate(authToken);
            User user = (User) authentication.getPrincipal();

            String accessToken = tokenService.generateToken(user);
            String refreshToken = tokenService.generateRefreshToken(user);
            user.setRefreshToken(refreshToken);
            userRepository.save(user);

            return ResponseEntity.ok(new TokenResponseDTO(accessToken, refreshToken));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bad credentials. Verify your Email and Password.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro. Please, try again later.");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@RequestBody @Valid RegisterRequestDTO data, UriComponentsBuilder uriBuilder) {
        return new ResponseEntity<>(adminService.register(data), HttpStatus.CREATED);
    }


    @PostMapping("/refresh-token")
    public ResponseEntity<TokenResponseDTO> refreshToken(@RequestParam String refreshToken) {
        Long userId = Long.valueOf(tokenService.tokenVerify(refreshToken));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String acessToken = tokenService.generateToken(user);
        String refreshTokenNew = tokenService.generateRefreshToken(user);
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
        return ResponseEntity.ok(new TokenResponseDTO(acessToken, refreshTokenNew));
    }

    @PostMapping("/verify-token")
    public ResponseEntity<Boolean> verifyToken(@RequestBody VerifyToken token) {
        boolean isValid = tokenService.tokenVerifyIsValid(token.token());
        if(isValid) {
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
    }
}
