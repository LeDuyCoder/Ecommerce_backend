package kiradev.studio.Eommerce.controller;

import io.swagger.v3.oas.annotations.Operation;
import kiradev.studio.Eommerce.dto.ChatMessage;
import kiradev.studio.Eommerce.dto.ChatResponse;
import kiradev.studio.Eommerce.entity.Conversation;
import kiradev.studio.Eommerce.entity.User;
import kiradev.studio.Eommerce.service.ChatService;
import kiradev.studio.Eommerce.service.ConversationService;
import kiradev.studio.Eommerce.service.UserService;
import kiradev.studio.Eommerce.utils.JwtUtil;
import kiradev.studio.Eommerce.websocket.AuthChannelInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/message")
public class chatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;
    private final ChatService chatService;
    private final JwtUtil jwtUtil;
    private final ConversationService conversationService;

    @Autowired
    private SimpUserRegistry simpUserRegistry;

    public chatController(SimpMessagingTemplate messagingTemplate, UserService userService, ChatService chatService, JwtUtil jwtUtil, ConversationService conversationService) {
        this.messagingTemplate = messagingTemplate;
        this.userService = userService;
        this.chatService = chatService;
        this.jwtUtil = jwtUtil;
        this.conversationService = conversationService;
    }


    @Operation(summary = "WebSocket endpoint: /app/chat", description = "Send message beetween muilti users")
    @GetMapping("/ws-info") // chỉ để hiển thị tài liệu thôi
    public String chatWebSocketInfo() {
        return """          
                            📘 WebSocket API Documentation
                            🔌 1. WebSocket Endpoint
                            - SockJS Endpoint (backend): `/ws`
                            - STOMP Send Destination: `/app/chat`
                            - STOMP Subscribe Destination: `/queue/messages-user{sessionId}`
                        
                            📤 2. Sending a Message to Another User
                            STOMP Send to: `/app/chat`
                        
                            Payload Example (ChatMessage):
                            {
                                "receiverEmail": "receiver@example.com",
                                "content": "Hello, how are you?"
                            }
                        
                            📥 3. Receiving a Message from Another User
                        
                            Subscribe Destination:
                            const sessionId = stompClient.ws._transport.url.split("/").slice(-2, -1)[0];
                            const destination = `/queue/messages-user${sessionId}`;
                        
                            STOMP Subscribe:
                            currentSubscription = stompClient.subscribe(destination, (message) => {
                                const msg = JSON.parse(message.body);
                                console.log("📥 Incoming message:", msg);
                            });
                        
                            Received Message Structure (ChatResponse):
                            {
                                "senderEmail": "sender@example.com",
                                "content": "Hello, how are you?"
                            }
                        
                            🔐 4. Connecting with JWT Token (Authorization Header)
                        
                            const socket = new SockJS('/ws');
                            const stompClient = Stomp.over(socket);
                            const token = "your-jwt-token";
                        
                            stompClient.connect(
                                { Authorization: "Bearer " + token },
                                function (frame) {
                                    console.log("✅ Successfully connected:", frame);
                        
                                    const sessionId = stompClient.ws._transport.url.split("/").slice(-2, -1)[0];
                                    const destination = `/queue/messages-user${sessionId}`;
                        
                                    currentSubscription = stompClient.subscribe(destination, function (message) {
                                        const msg = JSON.parse(message.body);
                                        console.log("📥 Incoming message:", msg);
                                    });
                        
                                    // Send a message
                                    stompClient.send("/app/chat", {}, JSON.stringify({
                                        receiverEmail: "receiver@example.com",
                                        content: "Hello!"
                                    }));
                                }
                            );
                        
                            📌 5. Notes
                            - The server maps userId to sessionId via `AuthChannelInterceptor.userIdToSessionId`.
                            - `sessionId` is extracted from the WebSocket URL after connection.
                            - Messages are delivered privately based on `sessionId`. This is a custom implementation; it does not use Spring STOMP's default `/user` prefix.
                        """;
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


    //write documentation for this method getAllChatBetweenTwoUsers
    /**
     * Retrieves all chat messages between two users.
     *
     * @param token         The JWT token for authentication.
     * @param receiverEmail The email of the receiver.
     * @param senderEmail   The email of the sender.
     * @return A ResponseEntity containing the chat history or an error message.
     */
    @GetMapping("/getAllChatBetweenTwoUsers")
    public ResponseEntity<?> getAllChatBetweenTwoUsers(@RequestHeader ("Authorization") String token,
                                                       @RequestParam("receiverEmail") String receiverEmail,
                                                       @RequestParam("senderEmail") String senderEmail) {

        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        User receiver = userService.findByEmail(receiverEmail).orElseThrow(() -> new RuntimeException("Receiver not found"));
        User sender = userService.findByEmail(senderEmail).orElseThrow(() -> new RuntimeException("Sender not found"));
        try {
            return ResponseEntity.ok(
                    Map.of("state", "success", "msg", "✅ Chat history retrieved successfully",
                            "data", chatService.findAllBySenderAndReceiver(sender, receiver))
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ Error retrieving chat history: " + e.getMessage()));
        }
    }

    /**
     * Retrieves a limited number of chat messages between two users.
     *
     * @param token         The JWT token for authentication.
     * @param receiverEmail The email of the receiver.
     * @param senderEmail   The email of the sender.
     * @param limit         The maximum number of messages to retrieve.
     * @return A ResponseEntity containing the limited chat history or an error message.
     */
    @GetMapping("/getAllChatBetweenTwoUsersLimit")
    public ResponseEntity<?> getAllChatBetweenTwoUsersLimit(@RequestHeader ("Authorization") String token,
                                                       @RequestParam("receiverEmail") String receiverEmail,
                                                       @RequestParam("senderEmail") String senderEmail,
                                                       @RequestParam("limit") int limit
    ) {

        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        if (limit <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("state", "fail", "msg", "❌ Limit must be greater than 0"));
        }

        User receiver = userService.findByEmail(receiverEmail).orElseThrow(() -> new RuntimeException("Receiver not found"));
        User sender = userService.findByEmail(senderEmail).orElseThrow(() -> new RuntimeException("Sender not found"));
        try {
            return ResponseEntity.ok(
                    Map.of("state", "success", "msg", "✅ Chat history retrieved successfully",
                            "data", chatService.findAllByBeetwenUsersLimit(sender, receiver, limit))
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ Error retrieving chat history: " + e.getMessage()));
        }
    }

    /**
     * Deletes a chat message by its ID.
     *
     * @param token The JWT token for authentication.
     * @param id    The UUID of the message to delete.
     * @return A ResponseEntity indicating success or failure.
     */
    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<?> deleteById(
            @RequestHeader("Authorization") String token,
            @PathVariable UUID id) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        try {
            chatService.deleteMessage(id);
            return ResponseEntity.ok(Map.of("state", "success", "msg", "✅ Message deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ Error deleting message: " + e.getMessage()));
        }
    }


    /**
     * 📘 WebSocket Chat API Documentation
     *
     * <p><b>🔌 1. WebSocket Endpoint</b><br>
     * - SockJS Endpoint: <code>/ws</code><br>
     * - STOMP Send Destination: <code>/app/chat</code><br>
     * - STOMP Subscribe Destination: <code>/queue/messages-user{sessionId}</code><br>
     *
     * <p><b>📤 2. Sending a Message to Another User</b><br>
     * Send to: <code>/app/chat</code><br>
     * Payload Example (ChatMessage):
     * <pre>{@code
     * {
     *   "receiverEmail": "receiver@example.com",
     *   "content": "Hello, how are you?"
     * }
     * }</pre>
     *
     * <p><b>📥 3. Receiving a Message</b><br>
     * Extract sessionId:
     * <pre>{@code
     * const sessionId = stompClient.ws._transport.url.split("/").slice(-2, -1)[0];
     * const destination = `/queue/messages-user${sessionId}`;
     * }</pre>
     *
     * Subscribe:
     * <pre>{@code
     * currentSubscription = stompClient.subscribe(destination, (message) => {
     *   const msg = JSON.parse(message.body);
     *   console.log("📥 Incoming message:", msg);
     * });
     * }</pre>
     *
     * Received Message Example (ChatResponse):
     * <pre>{@code
     * {
     *   "senderEmail": "sender@example.com",
     *   "content": "Hello, how are you?"
     * }
     * }</pre>
     *
     * <p><b>🔐 4. Connecting with JWT</b><br>
     * <pre>{@code
     * const socket = new SockJS('/ws');
     * const stompClient = Stomp.over(socket);
     * const token = "your-jwt-token";
     *
     * stompClient.connect(
     *   { Authorization: "Bearer " + token },
     *   function (frame) {
     *     const sessionId = stompClient.ws._transport.url.split("/").slice(-2, -1)[0];
     *     const destination = `/queue/messages-user${sessionId}`;
     *
     *     currentSubscription = stompClient.subscribe(destination, function (message) {
     *       const msg = JSON.parse(message.body);
     *       console.log("📥 Incoming message:", msg);
     *     });
     *
     *     stompClient.send("/app/chat", {}, JSON.stringify({
     *       receiverEmail: "receiver@example.com",
     *       content: "Hello!"
     *     }));
     *   }
     * );
     * }</pre>
     *
     * <p><b>📌 5. Notes</b><br>
     * - Server keeps track of user sessions via {@code AuthChannelInterceptor.userIdToSessionId}.<br>
     * - The {@code sessionId} is dynamically extracted after WebSocket connects.<br>
     * - Messages are routed privately by sessionId (custom logic, not the default Spring `/user` mechanism).
     */
    @MessageMapping("/chat") // Tức là client gửi đến /app/chat
    public void sendMessage(Principal sender, @Payload ChatMessage message) {
        User reciverUser = userService.findByEmail(message.getReceiverEmail()).get();
        User senderUser = userService.findById(UUID.fromString(sender.getName())).get();
        String sessionId = AuthChannelInterceptor.userIdToSessionId.get(reciverUser.getID().toString());

        message.setSenderEmail(senderUser.getEmail());

        Conversation conversation = conversationService.getConversationBySenderOrReceiver(senderUser, reciverUser);
        if(conversation == null) {
            conversationService.createConversation(senderUser, reciverUser, null, 0, 0);
            conversation = conversationService.getConversationBySenderOrReceiver(senderUser, reciverUser);
        }

        assert conversation != null;
        conversation.setLastMessage(message.getContent());
        if(senderUser.getID().equals(conversation.getUserOne().getID())){
            conversation.setUnreadCountMessagesOfUserTwo(conversation.getUnreadCountMessagesOfUserTwo() + 1);
        } else {
            conversation.setUnreadCountMessagesOfUserOne(conversation.getUnreadCountMessagesOfUserOne() + 1);
        }

        conversationService.saveConversation(conversation);

        chatService.addMessage(senderUser, reciverUser, message.getContent(), message.getCreatedAt(), conversation);

        if(sessionId != null) {
            messagingTemplate.convertAndSend("/queue/messages-user" + sessionId, new ChatResponse(message.getSenderEmail(), message.getContent()));
        }
    }
}
