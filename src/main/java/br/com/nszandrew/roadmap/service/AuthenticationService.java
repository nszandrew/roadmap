package br.com.nszandrew.roadmap.service;

import br.com.nszandrew.roadmap.model.user.User;
import lombok.AllArgsConstructor;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AuthenticationService {

    private final RoleHierarchy roleHierarchy;


    public boolean userNotHavePermission(User user, String selectedRole) {
        return user.getAuthorities().stream()
                .flatMap(autoridade -> roleHierarchy.getReachableGrantedAuthorities(List.of(autoridade))
                        .stream())
                .noneMatch(role -> role.getAuthority().equals(selectedRole));
    }

    public boolean userHasAdminPermission(User user) {
        for(GrantedAuthority authority: user.getAuthorities()){
            var findableAuth =  roleHierarchy.getReachableGrantedAuthorities(List.of(authority));

            for(GrantedAuthority perfil: findableAuth){
                if(perfil.getAuthority().equals("ROLE_ADMIN"))
                    return true;
            }
        }
        return false;
    }

    public boolean userHasBasicPermission(User user) {
        for(GrantedAuthority authority: user.getAuthorities()){
            var findableAuth =  roleHierarchy.getReachableGrantedAuthorities(List.of(authority));

            for(GrantedAuthority perfil: findableAuth){
                if(perfil.getAuthority().equals("ROLE_PAID_BASIC_TIER"))
                    return true;
            }
        }
        return false;
    }

    public boolean userHasPremiumPermission(User user) {
        for(GrantedAuthority authority: user.getAuthorities()){
            var findableAuth =  roleHierarchy.getReachableGrantedAuthorities(List.of(authority));

            for(GrantedAuthority perfil: findableAuth){
                if(perfil.getAuthority().equals("ROLE_PAID_PREMIUM_TIER"))
                    return true;
            }
        }
        return false;
    }

    public User getUserAuthenticated() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
