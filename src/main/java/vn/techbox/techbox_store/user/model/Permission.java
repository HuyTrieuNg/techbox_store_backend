package vn.techbox.techbox_store.user.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@Builder
@Table(name = "permissions")
@NoArgsConstructor
@AllArgsConstructor
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", unique = true, nullable = false, length = 100)
    private String name; // MODULE_USER_READ, MODULE_PRODUCT_WRITE, etc.

    @Column(name = "description")
    private String description;

    @Column(name = "module", nullable = false, length = 50)
    private String module; // USER, PRODUCT, ORDER, etc.

    @Column(name = "action", nullable = false, length = 20)
    private String action; // READ, WRITE, DELETE, UPDATE

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Many-to-many relationship with Role
    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", module='" + module + '\'' +
                ", action='" + action + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
