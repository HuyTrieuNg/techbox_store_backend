package vn.techbox.techbox_store.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.techbox.techbox_store.user.model.Account;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    @Query("SELECT COUNT(a) > 0 FROM Account a WHERE a.username = :username AND a.deletedAt IS NULL")
    boolean existsByUsername(@Param("username") String username);

    @Query("SELECT COUNT(a) > 0 FROM Account a WHERE a.email = :email AND a.deletedAt IS NULL")
    boolean existsByEmail(@Param("email") String email);

    @Query("SELECT a FROM Account a WHERE a.username = :username AND a.deletedAt IS NULL")
    Optional<Account> findByUsername(@Param("username") String username);

    @Query("SELECT a FROM Account a WHERE a.email = :email AND a.deletedAt IS NULL")
    Optional<Account> findByEmail(@Param("email") String email);
}
