package com.shadow2y.luthen.service.repository.stores;

import com.shadow2y.luthen.service.repository.tables.Permission;
import jakarta.inject.Inject;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class PermissionStore extends BaseDAO<Permission> {

    @Inject
    public PermissionStore(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Optional<Permission> getOrCreatePermission(String name, String description) {
        var result = getPermissions(name);
        if(result.isEmpty()) {
            Permission permission = new Permission(name);
            permission.setDescription(description);
            return Optional.of(persist(permission));
        }
        return result;
    }

    public List<Permission> getPermissions(Set<String> permissions) {
        return currentSession()
                .createNamedQuery("Permission.findByName", Permission.class)
                .setParameter("names", permissions)
                .getResultList();
    }

    public Optional<Permission> getPermissions(String permission) {
        var results = currentSession()
                .createNamedQuery("Permission.findByName", Permission.class)
                .setParameter("names", List.of(permission))
                .getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
    }


}
