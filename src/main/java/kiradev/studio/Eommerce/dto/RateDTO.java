package kiradev.studio.Eommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class RateDTO {
    private final UUID userId;
    private final UUID productId;
    private final int rate;
    private final String comment;
}
