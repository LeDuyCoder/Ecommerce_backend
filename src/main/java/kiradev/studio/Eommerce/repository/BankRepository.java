package kiradev.studio.Eommerce.repository;

import kiradev.studio.Eommerce.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BankRepository extends JpaRepository<Bank, UUID> {
    List<Bank> findByowner(String owner);
    List<Bank> findBynumberCard(String numberCard);
    List<Bank> findByuserID(UUID userID);
}
