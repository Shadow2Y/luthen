package com.shadow2y.luthen.service.repository.tables;

import com.shadow2y.luthen.api.summary.RoleSummary;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.util.Set;

@Data
@Entity
@Table(name = "roles")
@NamedQueries({
        @NamedQuery(name = "Role.getAllRoles", query = "SELECT r FROM Role r"),
        @NamedQuery(name = "Role.findByName", query = "SELECT r FROM Role r WHERE r.name IN :names")
})
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "role_permissions", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions;

    public RoleSummary toSummary() {
        var permissionSummaries = permissions.stream().map(Permission::toSummary).toList();
        return new RoleSummary(name, description, permissionSummaries);
    }

}
