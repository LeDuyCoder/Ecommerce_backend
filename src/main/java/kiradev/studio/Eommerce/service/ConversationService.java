package kiradev.studio.Eommerce.service;

import kiradev.studio.Eommerce.entity.Conversation;
import kiradev.studio.Eommerce.entity.User;
import kiradev.studio.Eommerce.repository.ConversationRepository;
import kiradev.studio.Eommerce.service.Interface.IConversationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ConversationService implements IConversationService {

    private final ConversationRepository conversationRepository;

    public ConversationService(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }


    /**
     * Creates a new conversation between two users.
     *
     * @param userOne                     The first user in the conversation.
     * @param userTwo                     The second user in the conversation.
     * @param lastMessage                 The last message sent in the conversation.
     * @param countUnreadMessagesForUserOne The count of unread messages for the first user.
     * @param countUnreadMessagesForUserTwo The count of unread messages for the second user.
     */
    @Override
    public void createConversation(User userOne, User userTwo, String lastMessage, int countUnreadMessagesForUserOne, int countUnreadMessagesForUserTwo) {
        Conversation conversation = new Conversation();
        conversation.setUserOne(userOne);
        conversation.setUserTwo(userTwo);
        conversation.setLastMessage(lastMessage);
        conversation.setUnreadCountMessagesOfUserOne(countUnreadMessagesForUserOne);
        conversation.setUnreadCountMessagesOfUserTwo(countUnreadMessagesForUserTwo);

        conversationRepository.save(conversation);
    }

    /**
     * Retrieves a conversation by either sender or receiver.
     *
     * @param sender   The user who sent the message.
     * @param receiver The user who received the message.
     * @return The conversation between the two users, or null if not found.
     */
    @Override
    public Conversation getConversationBySenderOrReceiver(User sender, User receiver) {
        return conversationRepository.findByUserOneOrUserTwo(sender, receiver) != null ?
                conversationRepository.findByUserOneOrUserTwo(sender, receiver) :
                conversationRepository.findByUserOneOrUserTwo(receiver, sender);
    }

    /**
     * Retrieves all conversations for a specific user.
     *
     * @param user The user whose conversations are to be retrieved.
     * @return A list of conversations involving the user.
     */
    @Override
    public List<Conversation> getAllConversationsByUser(User user) {
        return conversationRepository.findAllByUserOneOrUserTwo(user, user);
    }

    /**
     * Deletes a conversation by its ID.
     *
     * @param conversationId The ID of the conversation to be deleted.
     */
    @Override
    public void deleteConversation(UUID conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation with ID " + conversationId + " does not exist."));
        conversationRepository.delete(conversation);
    }

    /**
     * Updates an existing conversation with new details.
     *
     * @param conversationId                The ID of the conversation to be updated.
     * @param lastMessage                   The new last message for the conversation.
     * @param countUnreadMessagesForUserOne The new count of unread messages for the first user.
     * @param countUnreadMessagesForUserTwo The new count of unread messages for the second user.
     */
    @Override
    public void updateConversation(UUID conversationId, String lastMessage, int countUnreadMessagesForUserOne, int countUnreadMessagesForUserTwo) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation with ID " + conversationId + " does not exist."));

        if(lastMessage != null){
            conversation.setLastMessage(lastMessage);
        }

        if(countUnreadMessagesForUserOne >= 0){
            conversation.setUnreadCountMessagesOfUserOne(countUnreadMessagesForUserOne);
        }

        if(countUnreadMessagesForUserTwo >= 0){
            conversation.setUnreadCountMessagesOfUserTwo(countUnreadMessagesForUserTwo);
        }

        conversationRepository.save(conversation);
    }

    /**
     * Saves a conversation to the repository.
     *
     * @param conversation The conversation to be saved.
     */
    public void saveConversation(Conversation conversation) {
        conversationRepository.save(conversation);
    }
}
