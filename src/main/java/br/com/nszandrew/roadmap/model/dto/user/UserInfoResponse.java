package br.com.nszandrew.roadmap.model.dto.user;

import br.com.nszandrew.roadmap.model.user.User;

import java.util.List;

public record UserInfoResponse(UserDetailsDTO user, List<String> userRoles) {

    public UserInfoResponse(User user, List<String> userRoles) {
        this(new UserDetailsDTO(user), userRoles);
    }
}
