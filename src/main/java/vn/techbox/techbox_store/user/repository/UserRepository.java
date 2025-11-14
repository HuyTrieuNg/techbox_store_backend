package vn.techbox.techbox_store.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import vn.techbox.techbox_store.user.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>, PagingAndSortingRepository<User, Integer> {

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :id AND u.account.isActive = true")
    User findByIdWithRoles(@Param("id") Integer id);

    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.account " +
            "WHERE u.account.email = :email " +
            "AND u.account.isActive = true")
    Optional<User> findByAccountEmail(@Param("email") String email);

    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.account " +
            "LEFT JOIN FETCH u.addresses " +
            "WHERE u.account.email = :email " +
            "AND u.account.isActive = true")
    Optional<User> findByAccountEmailWithAddresses(@Param("email") String email);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.id = :id AND u.account.isActive = true")
    boolean existsByIdAndNotDeleted(@Param("id") Integer id);

    @Override
    @Query("SELECT u FROM User u WHERE u.account.isActive = true")
    @NonNull
    List<User> findAll();

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.addresses WHERE u.account.isActive = true")
    Page<User> findAllWithAddresses(Pageable pageable);

    @Override
    @Query("SELECT u FROM User u WHERE u.id = :id AND u.account.isActive = true")
    @NonNull
    Optional<User> findById(@Param("id") @NonNull Integer id);

    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findByIdIncludingDeleted(@Param("id") Integer id);

    @Query("SELECT DISTINCT u FROM User u " +
            "LEFT JOIN FETCH u.account " +
            "LEFT JOIN FETCH u.roles r " +
            "LEFT JOIN FETCH r.permissions " +
            "WHERE u.account.email = :email " +
            "AND u.account.isActive = true")
    User findByEmailForAuth(@Param("email") String email);

    @Query("SELECT DISTINCT u FROM User u " +
            "JOIN u.roles r " +
            "WHERE r.name = :roleName " +
            "AND u.account.isActive = true")
    Page<User> findByRoleName(@Param("roleName") String roleName, Pageable pageable);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.addresses")
    Page<User> findAllWithAddressesIncludingDeleted(Pageable pageable);

    @Query("SELECT DISTINCT u FROM User u " +
            "JOIN u.roles r " +
            "WHERE r.name = :roleName")
    Page<User> findByRoleNameIncludingDeleted(@Param("roleName") String roleName, Pageable pageable);
}
