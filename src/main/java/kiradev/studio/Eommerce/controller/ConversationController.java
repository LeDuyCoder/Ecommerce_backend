package kiradev.studio.Eommerce.controller;

import kiradev.studio.Eommerce.entity.Conversation;
import kiradev.studio.Eommerce.entity.User;
import kiradev.studio.Eommerce.service.ConversationService;
import kiradev.studio.Eommerce.service.UserService;
import kiradev.studio.Eommerce.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/conversation")
public class ConversationController {
    private final JwtUtil jwtUtil;
    private final ConversationService conversationService;
    private final UserService userService;

    public ConversationController(JwtUtil jwtUtil, ConversationService conversationService, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.conversationService = conversationService;
        this.userService = userService;
    }


    private ResponseEntity<?> validateToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("state", "fail", "msg", "❌ Missing or invalid token"));
        }

        String jwt = token.substring(7);
        String email = jwtUtil.extractEmail(jwt);

        if (!jwtUtil.validateToken(jwt, email)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("state", "fail", "msg", "❌ Token validation failed"));
        }

        return ResponseEntity.ok(email);
    }

    /**
     * Retrieves all conversations between two users.
     *
     * @param token         The JWT token for authentication.
     * @param senderEmail   The email of the sender.
     * @param receiverEmail The email of the receiver.
     * @return A ResponseEntity containing the list of conversations or an error message.
     */
    @GetMapping("/getAllConversations")
    public ResponseEntity<?> getConversations(@RequestHeader("Authorization") String token,
                                              @RequestParam("SenderEmail") String senderEmail,
                                              @RequestParam("ReceiverEmail") String receiverEmail
    ) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;

        User sender = userService.findByEmail(senderEmail)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userService.findByEmail(receiverEmail)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        try {
            return ResponseEntity.ok(Map.of("state", "success", "conversations", conversationService.getConversationBySenderOrReceiver(sender, receiver)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
        }
    }

    /**
     * Updates a conversation between two users.
     *
     * @param token                        The JWT token for authentication.
     * @param senderEmail                  The email of the sender.
     * @param receiverEmail                The email of the receiver.
     * @param message                      The message to be added to the conversation (optional).
     * @param countUnreadMessagesForSender The count of unread messages for the sender (optional).
     * @param countUnreadMessagesForReceiver The count of unread messages for the receiver (optional).
     * @return A ResponseEntity indicating success or failure of the update operation.
     */
    @PutMapping("/updateConversation")
    public ResponseEntity<?> updateConversation(@RequestHeader("Authorization") String token,
                                                @RequestParam("SenderEmail") String senderEmail,
                                                @RequestParam("ReceiverEmail") String receiverEmail,
                                                @RequestParam(value = "message", required = false) String message,
                                                @RequestParam(value = "countUnread", required = false) Integer countUnreadMessagesForSender,
                                                @RequestParam(value = "countUnread", required = false) Integer countUnreadMessagesForReceiver
    ) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;

        User userOne = userService.findByEmail(senderEmail)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User userTwo = userService.findByEmail(receiverEmail)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        try {
            Conversation conversation = conversationService.getConversationBySenderOrReceiver(userOne, userTwo);
            conversationService.updateConversation(conversation.getId(), message, countUnreadMessagesForSender, countUnreadMessagesForReceiver);
            return ResponseEntity.ok(Map.of("state", "success", "msg", "✅ Conversation updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
        }
    }
}
