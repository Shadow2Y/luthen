package com.shadow2y.luthen.service.repository.tables;

import com.shadow2y.luthen.api.summary.PermissionSummary;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "permissions")
@NamedQueries(
        @NamedQuery(name ="Permission.findByName", query ="SELECT p FROM Permission p WHERE p.name IN :names")
)
public class Permission {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    public PermissionSummary toSummary() {
        return new PermissionSummary(name, description);
    }

}
