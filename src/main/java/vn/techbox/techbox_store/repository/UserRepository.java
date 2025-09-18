package vn.techbox.techbox_store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.techbox.techbox_store.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
}
