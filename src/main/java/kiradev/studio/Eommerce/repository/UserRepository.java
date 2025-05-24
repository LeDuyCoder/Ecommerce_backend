package kiradev.studio.Eommerce.repository;

import kiradev.studio.Eommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>{
    Optional<User> findByname(String username);
    Optional<User> findByemail(String email);
    Optional<User> findByphone(String phoneNumber);
    Optional<User> findByaddress(String addrressUser);
    Optional<User> findByID(UUID id);
    void deleteById(UUID id);
}
