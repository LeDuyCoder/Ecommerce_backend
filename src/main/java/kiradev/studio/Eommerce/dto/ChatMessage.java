package kiradev.studio.Eommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ChatMessage {
    private String receiverEmail;
    private String senderEmail;
    private String content;
    private String createdAt;
}
