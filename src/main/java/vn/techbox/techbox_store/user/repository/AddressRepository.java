package vn.techbox.techbox_store.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.techbox.techbox_store.user.model.Address;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {

    @Query("SELECT a FROM Address a WHERE a.user.id = :userId AND a.deletedAt IS NULL")
    List<Address> findByUserIdAndNotDeleted(@Param("userId") Integer userId);

    @Query("SELECT a FROM Address a WHERE a.user.id = :userId AND a.isDefault = true AND a.deletedAt IS NULL")
    Optional<Address> findDefaultByUserId(@Param("userId") Integer userId);

    @Query("SELECT a FROM Address a WHERE a.id = :id AND a.deletedAt IS NULL")
    Optional<Address> findByIdAndNotDeleted(@Param("id") Integer id);

    @Query("SELECT a FROM Address a WHERE a.id = :id AND a.user.id = :userId AND a.deletedAt IS NULL")
    Optional<Address> findByIdAndUserIdAndNotDeleted(@Param("id") Integer id, @Param("userId") Integer userId);

    @Query("SELECT COUNT(a) FROM Address a WHERE a.user.id = :userId AND a.deletedAt IS NULL")
    long countByUserIdAndNotDeleted(@Param("userId") Integer userId);
}
