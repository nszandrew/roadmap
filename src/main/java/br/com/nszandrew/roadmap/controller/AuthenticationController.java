package br.com.nszandrew.roadmap.controller;

import br.com.nszandrew.roadmap.model.dto.user.LoginDTO;
import br.com.nszandrew.roadmap.model.dto.user.TokenResponseDTO;
import br.com.nszandrew.roadmap.model.user.User;
import br.com.nszandrew.roadmap.repository.user.UserRepository;
import br.com.nszandrew.roadmap.service.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserRepository userRepository;

    public AuthenticationController(AuthenticationManager authenticationManager, TokenService tokenService, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginDTO data) {
        try {
            var authToken = new UsernamePasswordAuthenticationToken(data.email(), data.password());
            var authentication = authenticationManager.authenticate(authToken);
            User user = (User) authentication.getPrincipal();

            String acessToken = tokenService.generateToken(user);
            String refreshToken = tokenService.generateRefreshToken(user);
            user.setRefreshToken(refreshToken);
            userRepository.save(user);

            return ResponseEntity.ok(new TokenResponseDTO(acessToken, refreshToken));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bad credentials. Verify your Email and Password.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro. Please, try again later.");
        }
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
}
