package br.com.nszandrew.roadmap.service;

import br.com.nszandrew.roadmap.infra.email.EmailSender;
import br.com.nszandrew.roadmap.infra.exceptions.CustomException;
import br.com.nszandrew.roadmap.model.dto.user.RegisterRequestDTO;
import br.com.nszandrew.roadmap.model.dto.user.RegisterResponseDTO;
import br.com.nszandrew.roadmap.model.dto.user.UserDetailsDTO;
import br.com.nszandrew.roadmap.model.user.PlanType;
import br.com.nszandrew.roadmap.model.user.Role;
import br.com.nszandrew.roadmap.model.user.User;
import br.com.nszandrew.roadmap.repository.user.RoleRepository;
import br.com.nszandrew.roadmap.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AdminService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final EmailSender emailSender;
    private final AuthenticationService authenticationService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmailIgnoreCaseAndIsVerifyEmailTrue(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Transactional
    public RegisterResponseDTO register(@Valid RegisterRequestDTO data) {
        Optional<User> user = userRepository.findByEmailIgnoreCaseAndIsVerifyEmailTrue(data.email());

        if(user.isPresent()){
            throw new CustomException("Email already exists");
        }

        var role = roleRepository.findByRole(Role.PAID_BASIC_TIER);
        var password = passwordEncoder.encode(data.password());

        User newUser = new User(data, password, role);
        userRepository.save(newUser);

        emailSender.sendVerifyEmail(newUser);
        return new RegisterResponseDTO(true, "User successfully registered!");
    }

    @Transactional
    public void verifyEmail(String code) {
        var user = userRepository.findByVerifyToken(code)
                .orElseThrow(() -> new CustomException("Code is valid"));

        user.verify();
        userRepository.save(user);

    }

    @Transactional
    public void changeRole(Long id, Role role) {
        verifyIfIsAdmin();
        var user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException("User not found"));
        var roleChange = roleRepository.findByRole(role);

        if(user.getPlanType() == PlanType.FREE_TIER){
            if(role == Role.PAID_BASIC_TIER){user.setPlanType(PlanType.BASIC_TIER);}
            if(role == Role.PAID_PREMIUM_TIER){user.setPlanType(PlanType.PREMIUM_TIER);}
        }

        if(user.getPlanType() == PlanType.BASIC_TIER){
            if(role == Role.PAID_PREMIUM_TIER){user.setPlanType(PlanType.PREMIUM_TIER);}
            if(role == Role.PAID_BASIC_TIER){user.setPlanType(PlanType.BASIC_TIER);}
        }

        if(role == Role.ADMIN){
            user.setPlanType(PlanType.PREMIUM_TIER);
        }

        user.addProfile(roleChange);
        userRepository.save(user);
    }

    @Transactional
    public void removeRole(Long id, Role role) {
        verifyIfIsAdmin();
        var user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException("User not found"));
        var roleChange = roleRepository.findByRole(role);

        user.setPlanType(PlanType.FREE_TIER);
        user.removeProfile(roleChange);
        userRepository.save(user);
    }


    public List<UserDetailsDTO> getAllUsers() {
        verifyIfIsAdmin();
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> new UserDetailsDTO(user))
                .toList();
    }

    private void verifyIfIsAdmin() {
        if(Boolean.FALSE.equals(authenticationService.userHasAdminPermission(authenticationService.getUserAuthenticated()))){
            throw new CustomException("Access denied!");
        }
    }
}
