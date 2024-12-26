package br.com.nszandrew.roadmap.service;

import br.com.nszandrew.roadmap.infra.exceptions.CustomException;
import br.com.nszandrew.roadmap.model.dto.UserDetailsDTO;
import br.com.nszandrew.roadmap.model.dto.UserUpdateDTO;
import br.com.nszandrew.roadmap.model.user.User;
import br.com.nszandrew.roadmap.repository.user.RoleRepository;
import br.com.nszandrew.roadmap.repository.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationService authenticationService;
    private final PasswordEncoder passwordEncoder;

    /*
    Necessita adicionar mais parametros para retorno como RoadMaps/RoadmapsItens e os Roles
     */
    public UserDetailsDTO getUser() {
        User user = authenticationService.getUserAuthenticated();
        userRepository.findById(user.getId()).orElseThrow(() -> new CustomException("User not found"));

        return new UserDetailsDTO(user);
    }

    @Transactional
    public String editUser(UserUpdateDTO data) {
        User user = authenticationService.getUserAuthenticated();

        if(data.currentPassword().isBlank() && data.newPassword().isBlank()) {
            user.updateUser(data);
            userRepository.save(user);
            return "Usuário editado com sucesso";
        } else {
            if (!passwordEncoder.matches(data.currentPassword(), user.getPassword())) {
                throw new CustomException("Senha atual incorreta");
            }
            user.updateUser(data, passwordEncoder.encode(data.newPassword()));
            userRepository.save(user);
            return "Usuário editado com sucesso";
        }
    }
}
