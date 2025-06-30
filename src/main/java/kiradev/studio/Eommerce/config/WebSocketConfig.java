package kiradev.studio.Eommerce.config;

import kiradev.studio.Eommerce.websocket.AuthChannelInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final AuthChannelInterceptor authChannelInterceptor;

    public WebSocketConfig(AuthChannelInterceptor authChannelInterceptor) {
        this.authChannelInterceptor = authChannelInterceptor;
    }


    /**
     * Configures the message broker used for routing messages between clients and the server.
     *
     * <p>This method sets up both the message broker destinations (used for subscriptions)
     * and the application destination prefix (used for messages sent to controller methods).</p>
     *
     * <p><strong>Configurations:</strong></p>
     * <ul>
     *   <li><b>enableSimpleBroker("/topic", "/queue", "/user")</b>:
     *       Enables a simple in-memory message broker to carry messages back to the client
     *       on destinations prefixed with "/topic", "/queue", and "/user".</li>
     *   <li><b>setApplicationDestinationPrefixes("/app")</b>:
     *       Specifies the prefix used for messages bound for {@code @MessageMapping} annotated methods.
     *       For example, messages sent to "/app/chat" will be routed to a method like {@code @MessageMapping("/chat")}.</li>
     *   <li><b>setUserDestinationPrefix("/user")</b>:
     *       Defines the prefix used for user-specific messages. Enables use of destinations like
     *       {@code /user/{sessionId}/queue/messages} to target individual users.</li>
     * </ul>
     *
     * @param config the {@link MessageBrokerRegistry} to be configured
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue", "/user");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    /**
     * Registers STOMP endpoints that the clients will use to connect to the WebSocket server.
     *
     * <p>This configuration allows clients to connect to the server via the {@code /ws} endpoint,
     * and also enables SockJS fallback options for browsers that don't support native WebSocket.</p>
     *
     * <p><strong>Configurations:</strong></p>
     * <ul>
     *   <li><b>addEndpoint("/ws")</b>: Defines the endpoint URL that clients use to initiate WebSocket connections.</li>
     *   <li><b>setAllowedOriginPatterns("*")</b>: Allows cross-origin requests from any origin.
     *       For production, consider restricting to specific domains.</li>
     *   <li><b>withSockJS()</b>: Enables SockJS support for fallback transports (e.g., XHR, long-polling) if WebSocket is not available.</li>
     * </ul>
     *
     * @param registry the {@link StompEndpointRegistry} to register endpoints on
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }

    /**
     * Configures the channel used for processing inbound messages from WebSocket clients.
     *
     * <p>This method registers a custom {@link ChannelInterceptor}, such as {@code authChannelInterceptor},
     * which can be used to authenticate or manipulate messages before they reach the controller layer.</p>
     *
     * <p><strong>Use cases:</strong></p>
     * <ul>
     *   <li>Extract and validate JWT tokens from STOMP headers</li>
     *   <li>Assign user principals to messages</li>
     *   <li>Log or modify message content before processing</li>
     * </ul>
     *
     * @param registration the {@link ChannelRegistration} for customizing the inbound channel
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(authChannelInterceptor); // ✅ Đăng ký interceptor
    }
}
