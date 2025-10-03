package com.shadow2y.luthen.service.repository.stores;

import com.shadow2y.luthen.service.repository.tables.Permission;
import com.shadow2y.luthen.service.repository.tables.Role;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Set;

public class RoleStore extends BaseDAO<Role> {

    public RoleStore(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Role save(Role role) {
        return persist(role);
    }

    public Role updateOrCreateRole(String name, String description, Set<Permission> permissions) {
        var result = getRoles(List.of(name));
        Role role;
        if(result.isEmpty()) {
            role = new Role();
            role.setName(name);
        } else {
            role = result.getFirst();
        }
        role.setDescription(description);
        role.setPermissions(permissions);
        return persist(role);
    }

    public List<Role> getRoles(List<String> roleNames) {
        return currentSession().createNamedQuery("Role.findByName", Role.class)
                .setParameter("names", roleNames)
                .getResultList();
    }

    public List<Role> getAllRoles() {
        return currentSession().createNamedQuery("Role.getAllRoles", Role.class)
                .getResultList();
    }

}
