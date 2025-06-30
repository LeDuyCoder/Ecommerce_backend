package kiradev.studio.Eommerce.service.Interface;

import kiradev.studio.Eommerce.entity.Conversation;
import kiradev.studio.Eommerce.entity.User;

import java.util.List;
import java.util.UUID;

public interface IConversationService {
    void createConversation(User userOne, User userTwo, String lastMessage, int countUnreadMessagesForUserOne, int countUnreadMessagesForUserTwo);
    Conversation getConversationBySenderOrReceiver(User userOne, User UserTwo);

    List<Conversation> getAllConversationsByUser(User user);

    void deleteConversation(UUID conversationId);
    void updateConversation(UUID conversationId, String lastMessage, int countUnreadMessagesForUserOne, int countUnreadMessagesForUserTwo);
}
