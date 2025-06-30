package kiradev.studio.Eommerce.repository;

import kiradev.studio.Eommerce.entity.LogAdmin;
import kiradev.studio.Eommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LogAdminRepository extends JpaRepository<LogAdmin, UUID> {
    List<LogAdmin> findByUser(User user);
}
