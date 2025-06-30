package kiradev.studio.Eommerce.websocket;

import kiradev.studio.Eommerce.entity.User;
import kiradev.studio.Eommerce.service.UserService;
import kiradev.studio.Eommerce.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuthChannelInterceptor implements ChannelInterceptor {

    @Autowired
    private final JwtUtil jwtUtil; // hoặc dịch vụ decode token của bạn
    private final UserService userService;
    public final static Map<String, String> userIdToSessionId = new ConcurrentHashMap<>();

    public AuthChannelInterceptor(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    /**
     * Intercepts and processes STOMP CONNECT messages to perform JWT authentication.
     *
     * <p>This method is triggered when a WebSocket client attempts to establish a STOMP connection.
     * It extracts the JWT token from the "Authorization" header, validates it, and authenticates
     * the user based on the extracted email.</p>
     *
     * <p>If authentication succeeds, it stores the mapping between the user's ID and the session ID
     * to enable targeted messaging later. It also sets the authenticated user into the STOMP accessor
     * to associate the session with the user.</p>
     *
     * <p><strong>Steps performed:</strong></p>
     * <ol>
     *   <li>Check if the STOMP command is {@code CONNECT}.</li>
     *   <li>Retrieve the JWT token from the "Authorization" header.</li>
     *   <li>Remove the "Bearer " prefix from the token.</li>
     *   <li>Extract the email from the JWT token using {@code jwtUtil}.</li>
     *   <li>Find the user in the database using the extracted email.</li>
     *   <li>Set the user ID (as {@link java.security.Principal}) in the STOMP accessor.</li>
     *   <li>Store the user ID and session ID mapping in {@code userIdToSessionId} map for message routing.</li>
     * </ol>
     *
     * @param message the incoming STOMP message
     * @param channel the message channel
     * @return the processed message with user authentication info attached
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");

            String jwt = token.substring(7); // Loại bỏ "Bearer "
            String email = jwtUtil.extractEmail(jwt); // Lấy email từ token
            User user = userService.findByEmail(email).get();

            accessor.setUser(new UsernamePasswordAuthenticationToken(user.getID().toString(), null, List.of()));
            userIdToSessionId.put(user.getID().toString(), accessor.getSessionId());
            System.out.println(Objects.requireNonNull(accessor.getUser()).getName());
        }

        return message;
    }
}