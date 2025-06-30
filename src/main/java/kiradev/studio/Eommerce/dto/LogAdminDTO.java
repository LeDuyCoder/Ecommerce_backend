package kiradev.studio.Eommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class LogAdminDTO {
    private UUID id;
    private UUID userAdminId;
    private String log;
    private String createdAt;
}
