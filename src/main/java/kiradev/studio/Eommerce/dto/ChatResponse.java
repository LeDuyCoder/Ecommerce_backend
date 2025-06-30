package kiradev.studio.Eommerce.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatResponse {
    private String senderEmail;
    private String content;

    public ChatResponse(String senderEmail, String content) {
        this.senderEmail = senderEmail;
        this.content = content;
    }

    // getters và setters
}
