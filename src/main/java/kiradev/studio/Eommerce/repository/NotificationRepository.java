package kiradev.studio.Eommerce.repository;

import kiradev.studio.Eommerce.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findBymail(String mail);

}
