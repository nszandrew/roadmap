package br.com.nszandrew.roadmap.model.user;

import br.com.nszandrew.roadmap.model.dto.user.RegisterRequestDTO;
import br.com.nszandrew.roadmap.model.dto.user.UserUpdateDTO;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanType planType;

    private Boolean active = true;

    private String refreshToken;
    private LocalDateTime refreshTokenExpiresAt;

    private boolean isVerifyEmail = false;
    private String verifyToken;
    private LocalDateTime verifyTokenExpiresAt;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tb_user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<UserRole> userRole = new ArrayList<>();

    public User(@Valid RegisterRequestDTO data, String password, UserRole role) {
        this.name = data.name();
        this.email = data.email();
        this.password = password;
        this.planType = PlanType.FREE_TIER;
        this.active = true;
        this.isVerifyEmail = false;
        this.verifyToken = UUID.randomUUID().toString();
        this.verifyTokenExpiresAt = LocalDateTime.now().plusMinutes(15);
        this.refreshToken = null;
        this.refreshTokenExpiresAt = null;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = null;
        this.userRole.add(role);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userRole;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    public void verify() {
        if(verifyTokenExpiresAt.isBefore(LocalDateTime.now())){
            throw new RuntimeException("Token expirado");
        }
        this.isVerifyEmail = true;
        this.verifyToken = null;
        this.verifyTokenExpiresAt = null;
    }

    public void addProfile(UserRole roleChange) {
        this.userRole.add(roleChange);
    }

    public void removeProfile(UserRole roleChange) {
        this.userRole.remove(roleChange);
    }

    public void updateUser(UserUpdateDTO data) {
        this.name = data.name();
        this.active = data.active();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateUser(UserUpdateDTO data, String password) {
        this.name = data.name();
        this.password = password;
        this.active = data.active();
        this.updatedAt = LocalDateTime.now();
    }
}
