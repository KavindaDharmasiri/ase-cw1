package lk.ase.kavinda.islandlink.entity;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "permissions")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private Module module;

    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles;

    public Permission() {}

    public Permission(String name, String description, Module module) {
        this.name = name;
        this.description = description;
        this.module = module;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Module getModule() { return module; }
    public void setModule(Module module) { this.module = module; }

    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }

    public enum Module {
        INVENTORY, LOGISTICS, ACCOUNTS, ORDERS, REPORTS, USER_MANAGEMENT, PRODUCTS, PROCUREMENT
    }
}