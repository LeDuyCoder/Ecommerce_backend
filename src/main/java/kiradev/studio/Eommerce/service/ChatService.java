package kiradev.studio.Eommerce.service;

import kiradev.studio.Eommerce.dto.ChatDTO;
import kiradev.studio.Eommerce.entity.Chat;
import kiradev.studio.Eommerce.entity.Conversation;
import kiradev.studio.Eommerce.entity.User;
import kiradev.studio.Eommerce.repository.ChatRepository;
import kiradev.studio.Eommerce.service.Interface.IChatService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

@Service
public class ChatService implements IChatService {
    private final ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    /**
     * Adds a new message to the chat between two users.
     *
     * @param sender      The user who sends the message.
     * @param receiver    The user who receives the message.
     * @param content     The content of the message.
     * @param timestamp   The timestamp when the message was sent.
     * @param conversation The conversation in which the message is being sent.
     */
    @Override
    public void addMessage(User sender, User receiver, String content, String timestamp, Conversation conversation) {
        Chat chat = new Chat();
        chat.setMessage(content);
        chat.setUserSender(sender);
        chat.setUserReceiver(receiver);
        chat.setCreatedAt(timestamp);
        chat.setConversation(conversation);

        chatRepository.save(chat);
    }

    /**
     * Updates the content of an existing message.
     *
     * @param messageId The ID of the message to be updated.
     * @param content   The new content for the message.
     */
    @Override
    public void updateMessage(UUID messageId, String content) {
        Chat chat = chatRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message with ID " + messageId + " does not exist."));
        chat.setMessage(content);
        chatRepository.save(chat);

    }

    /**
     * Deletes a message by its ID.
     *
     * @param messageId The ID of the message to be deleted.
     */
    @Override
    public void deleteMessage(UUID messageId) {
        Chat chat = chatRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message with ID " + messageId + " does not exist."));
        chatRepository.delete(chat);
    }

    /**
     * Finds all chat messages between two users.
     *
     * @param sender   The user who sent the messages.
     * @param receiver The user who received the messages.
     * @return A list of ChatDTO objects representing the chat messages.
     */
    @Override
    public List<ChatDTO> findAllBySenderAndReceiver(User sender, User receiver) {
        return chatRepository.findAllChatsBetweenUsers(sender, receiver)
                .stream()
                .map(chat -> new ChatDTO(chat.getId(), chat.getUserSender().getID(), chat.getUserReceiver().getID(), chat.getMessage(), chat.getCreatedAt()))
                .toList();
    }

    /**
     * Finds the most recent chat messages between two users, limited to a specified number.
     *
     * @param sender   The user who sent the messages.
     * @param receiver The user who received the messages.
     * @param limit    The maximum number of messages to retrieve.
     * @return A list of ChatDTO objects representing the most recent chat messages.
     */
    public List<ChatDTO> findAllByBeetwenUsersLimit(User sender, User receiver, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return chatRepository.findTopByUsers(sender, receiver, pageable)
                .stream()
                .map(chat -> new ChatDTO(chat.getId(), chat.getUserSender().getID(), chat.getUserReceiver().getID(), chat.getMessage(), chat.getCreatedAt()))
                .toList();
    }
}
