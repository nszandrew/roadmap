package br.com.nszandrew.roadmap.repository.user;

import br.com.nszandrew.roadmap.model.user.Role;
import br.com.nszandrew.roadmap.model.user.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoleRepository extends JpaRepository<UserRole, Long> {

    UserRole findByRole(Role role);
}
