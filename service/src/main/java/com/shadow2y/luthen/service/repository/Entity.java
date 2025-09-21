package com.shadow2y.luthen.service.repository;

import com.shadow2y.luthen.service.model.dto.UserDto;
import com.shadow2y.luthen.service.model.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@jakarta.persistence.Entity
@Table(name = "entities")
@NamedQueries({
        @NamedQuery(name = "Entity.findAll", query = "SELECT u FROM Entity u"),
        @NamedQuery(name = "Entity.findByName", query = "SELECT u FROM Entity u WHERE u.username LIKE :name"),
        @NamedQuery(name = "Entity.findByStatus", query = "SELECT u FROM Entity u WHERE u.status = :status")
})
public class Entity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email", nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime created_at;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public UserDto toDTO() {
        return new UserDto(
                id,
                username,
                email,
                status,
                created_at
        );
    }
}
