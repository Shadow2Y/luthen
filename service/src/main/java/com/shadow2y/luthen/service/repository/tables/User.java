package com.shadow2y.luthen.service.repository.tables;

import com.shadow2y.luthen.api.summary.UserSummary;
import com.shadow2y.luthen.api.models.UserStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "users")
@NamedQueries({
        @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
        @NamedQuery(name = "User.findByEmail", query = "SELECT u FROM User u WHERE u.email LIKE :email"),
        @NamedQuery(name = "User.findByStatus", query = "SELECT u FROM User u WHERE u.status = :status"),
        @NamedQuery(name = "User.findByUsername", query = "SELECT u FROM User u WHERE u.username LIKE :username")
})
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "username", nullable = false)
    private String username;

    @Setter
    @Column(name = "email", nullable = false)
    private String email;

    @Setter
    @Column(name = "password_hash", nullable = false)
    private String password;

    @Setter
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime created_at;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Getter
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;

    public User(String username, String email, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.status = UserStatus.ACTIVE;
    }

    public List<String> getRoleNames() {
        return roles.stream().map(Role::getName).toList();
    }

    public UserSummary toSummary() {
        var roleSummaries = roles.stream().map(Role::toSummary).toList();
        return new UserSummary(username, email, status, roleSummaries, created_at);
    }

}
