package vn.techbox.techbox_store.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.techbox.techbox_store.user.model.Role;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByName(String name);

    @Query("SELECT r FROM Role r LEFT JOIN FETCH r.permissions WHERE r.name = :name")
    Optional<Role> findByNameWithPermissions(@Param("name") String name);

    @Query("SELECT r FROM Role r LEFT JOIN FETCH r.permissions")
    List<Role> findAllWithPermissions();

    boolean existsByName(String name);
}
