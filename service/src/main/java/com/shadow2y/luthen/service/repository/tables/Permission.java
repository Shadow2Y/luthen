package com.shadow2y.luthen.service.repository.tables;

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

    public Permission(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Permission other)) return false;
        return name.equals(other.name);
    }

}
