package br.com.nszandrew.roadmap.repository.user;

import br.com.nszandrew.roadmap.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.isVerifyEmail = true")
    Optional<User> findByEmailIgnoreCaseAndIsVerifyEmailTrue(@Param("email") String email);

    Optional<User> findByVerifyToken(String code);
}
