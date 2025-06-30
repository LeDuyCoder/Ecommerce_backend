package kiradev.studio.Eommerce.service.Interface;

import kiradev.studio.Eommerce.dto.ChatDTO;
import kiradev.studio.Eommerce.entity.Chat;
import kiradev.studio.Eommerce.entity.Conversation;
import kiradev.studio.Eommerce.entity.User;

import java.util.List;
import java.util.UUID;

public interface IChatService {
    void addMessage(User sender, User receiver, String content, String timestamp, Conversation conversation);
    void updateMessage(UUID messageId, String content);
    void deleteMessage(UUID messageId);
    List<ChatDTO> findAllBySenderAndReceiver(User sender, User receiver);
}
