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
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@NamedQueries({
        @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
        @NamedQuery(name = "User.findByEmail", query = "SELECT u FROM User u WHERE u.email = :email"),
        @NamedQuery(name = "User.findByStatus", query = "SELECT u FROM User u WHERE u.status = :status"),
        @NamedQuery(name = "User.findByUsername", query = "SELECT u FROM User u WHERE u.username = :username")
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

    @Transient
    private List<String> roleNames;

    @Transient
    private UserSummary userSummary;

    public User(String username, String email, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.status = UserStatus.INACTIVE;
    }

    public List<String> getRoleNames() {
        if(this.roleNames != null) return roleNames;
        this.roleNames = roles.stream().map(Role::getName).toList();
        return roleNames;
    }

    public UserSummary toSummary() {
        if(userSummary!=null) return userSummary;
        this.userSummary = new UserSummary(username, email, status, getRoleNames(), created_at);
        return userSummary;
    }

}
