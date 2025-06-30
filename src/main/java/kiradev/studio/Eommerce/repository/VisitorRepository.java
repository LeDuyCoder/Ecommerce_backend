package kiradev.studio.Eommerce.repository;

import kiradev.studio.Eommerce.entity.User;
import kiradev.studio.Eommerce.entity.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface VisitorRepository extends JpaRepository<Visitor, UUID> {
    List<Visitor> findAllByCreatedAt(LocalDate createdAt);
    List<Visitor> findAllByCreatedAtBetween(LocalDate startDate, LocalDate endDate);
    Visitor findByUserAndCreatedAt(User user, LocalDate createdAt);
}
