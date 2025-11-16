package com.shadow2y.luthen.service.repository.stores;

import com.shadow2y.luthen.service.repository.tables.Role;
import jakarta.inject.Inject;
import org.hibernate.SessionFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class RoleStore extends BaseDAO<Role> {

    final PermissionStore permissionStore;

    @Inject
    public RoleStore(PermissionStore permissionStore, SessionFactory sessionFactory) {
        super(sessionFactory);
        this.permissionStore = permissionStore;
    }

    public Role save(Role role) {
        return persist(role);
    }

    public Role updateOrCreateRole(String name, String description, Set<String> permissions) {
        var result = getRoles(List.of(name));
        Role role;
        if(result.isEmpty()) {
            role = new Role();
            role.setName(name);
        } else {
            role = result.getFirst();
        }
        role.setDescription(description);
        role.setPermissions(new HashSet<>(permissionStore.getPermissions(permissions)));
        return persist(role);
    }

    public List<Role> getRoles(List<String> roleNames) {
        return currentSession().createNamedQuery("Role.findByName", Role.class)
                .setParameter("names", roleNames)
                .getResultList();
    }

    public Optional<Role> getRole(String roleNames) {
        Role role;
        try {
            role = currentSession().createNamedQuery("Role.findByNameExact", Role.class)
                    .setParameter("name", roleNames)
                    .getSingleResult();
        } catch (Exception e) {
            return Optional.empty();
        }
        return role==null ? Optional.empty() : Optional.of(role);
    }

    public List<Role> getAllRoles() {
        return currentSession().createNamedQuery("Role.getAllRoles", Role.class)
                .getResultList();
    }

    public Set<String> getPermissions(Role role) {
        return role.getPermissions();
    }

}
