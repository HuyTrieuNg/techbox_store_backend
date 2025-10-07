package vn.techbox.techbox_store.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.techbox.techbox_store.user.model.Permission;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Integer> {

    Optional<Permission> findByName(String name);

    List<Permission> findByModule(String module);

    List<Permission> findByAction(String action);

    @Query("SELECT p FROM Permission p WHERE p.module = :module AND p.action = :action")
    Optional<Permission> findByModuleAndAction(@Param("module") String module, @Param("action") String action);

    boolean existsByName(String name);
}
