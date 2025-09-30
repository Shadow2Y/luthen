package com.shadow2y.luthen.service.repository.stores;

import com.shadow2y.luthen.service.repository.tables.Permission;
import org.hibernate.SessionFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class PermissionStore extends BaseDAO<Permission> {

    public PermissionStore(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Optional<Permission> getOrCreatePermission(String name, String description) {
        var result = getPermissions(name);
        if(result.isEmpty()) {
            Permission permission = new Permission();
            permission.setName(name);
            permission.setDescription(description);
            return Optional.of(persist(permission));
        }
        return result;
    }

    public Set<Permission> getPermissions(List<String> permissions) {
        return new HashSet<>(
                currentSession().createNamedQuery("Permission.findByName", Permission.class)
                .setParameter("names", permissions)
                .getResultList()
        );
    }

    public Optional<Permission> getPermissions(String permission) {
        var results = currentSession()
                .createNamedQuery("Permission.findByName", Permission.class)
                .setParameter("names", List.of(permission))
                .getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
    }


}
