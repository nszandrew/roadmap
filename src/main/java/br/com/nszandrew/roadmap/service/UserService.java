package br.com.nszandrew.roadmap.service;

import br.com.nszandrew.roadmap.infra.email.EmailSender;
import br.com.nszandrew.roadmap.model.dto.RegisterRequestDTO;
import br.com.nszandrew.roadmap.model.user.User;
import br.com.nszandrew.roadmap.repository.Roadmap.RoadMapItemRepository;
import br.com.nszandrew.roadmap.repository.Roadmap.RoadMapRepository;
import br.com.nszandrew.roadmap.repository.payment.PaymentRepository;
import br.com.nszandrew.roadmap.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.mail.MailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoadMapRepository roadMapRepository;
    private final RoadMapItemRepository roadMapItemRepository;
    private final PaymentRepository paymentRepository;
    private final EmailSender emailSender;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmailIgnoreCaseAndIsVerifyEmailTrue(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Transactional
    public String register(@Valid RegisterRequestDTO data) {
        Optional<User> user = userRepository.findByEmailIgnoreCaseAndIsVerifyEmailTrue(data.email());

        if(user.isPresent()){
            throw new RuntimeException("Email already exists");
        }
        var password = passwordEncoder.encode(data.password());

        User newUser = new User(data, password);
        userRepository.save(newUser);

        emailSender.sendVerifyEmail(newUser);
        return "Usuario criado com sucesso!";
    }

    @Transactional
    public void verifyEmail(String code) {
        var user = userRepository.findByVerifyToken(code)
                .orElseThrow(() -> new RuntimeException("Code is valid"));

        user.verify();
        userRepository.save(user);

    }
}
