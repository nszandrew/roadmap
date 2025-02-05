package br.com.nszandrew.roadmap.service;

import br.com.nszandrew.roadmap.infra.exceptions.CustomException;
import br.com.nszandrew.roadmap.model.dto.user.UserInfoResponse;
import br.com.nszandrew.roadmap.model.dto.user.UserUpdateDTO;
import br.com.nszandrew.roadmap.model.user.User;
import br.com.nszandrew.roadmap.model.user.UserRole;
import br.com.nszandrew.roadmap.repository.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final PasswordEncoder passwordEncoder;

    public UserInfoResponse getUserInfo() {
        User user = authenticationService.getUserAuthenticated();
        userRepository.findById(user.getId()).orElseThrow(() -> new CustomException("User not found"));

        List<UserRole> roles = user.getUserRole();
        List<String> userRole = roles.stream()
                .map(role -> role.getRole().toString())
                .toList();

        return new UserInfoResponse(user, userRole);
    }

    @Transactional
    public String editUser(UserUpdateDTO data) {
        User user = authenticationService.getUserAuthenticated();
        if(!user.getId().equals(data.id())){
            throw new CustomException("User not has been updated");
        }

        if(data.currentPassword().isBlank() && data.newPassword().isBlank()) {
            user.updateUser(data);
            userRepository.save(user);
            return "User edit successful";
        } else {
            if (!passwordEncoder.matches(data.currentPassword(), user.getPassword())) {
                throw new CustomException("Senha atual incorreta");
            }
            user.updateUser(data, passwordEncoder.encode(data.newPassword()));
            userRepository.save(user);
            return "User edit successful";
        }
    }
}
