package kiradev.studio.Eommerce.repository;

import kiradev.studio.Eommerce.entity.Conversation;
import kiradev.studio.Eommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    Conversation findByUserOneOrUserTwo(User UserOne, User UserTwo);
    List<Conversation> findAllByUserOneOrUserTwo(User user1, User user2);
}
