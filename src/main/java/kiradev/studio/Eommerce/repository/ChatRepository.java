package kiradev.studio.Eommerce.repository;

import kiradev.studio.Eommerce.entity.Chat;
import kiradev.studio.Eommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ChatRepository extends JpaRepository<Chat, UUID> {
    List<Chat> findAllById(UUID id);
    @Query("SELECT c FROM Chat c WHERE " +
            "(c.userSender = :user1 AND c.userReceiver = :user2) " +
            "OR (c.userSender = :user2 AND c.userReceiver = :user1) " +
            "ORDER BY c.createdAt ASC")
    List<Chat> findAllChatsBetweenUsers(@Param("user1") User user1, @Param("user2") User user2);

    @Query("SELECT c FROM Chat c WHERE " +
            "(c.userSender = :user1 AND c.userReceiver = :user2) " +
            "OR (c.userSender = :user2 AND c.userReceiver = :user1) " +
            "ORDER BY c.createdAt ASC")
    List<Chat> findTopByUsers(@Param("user1") User user1,
                              @Param("user2") User user2,
                              Pageable pageable);

}
