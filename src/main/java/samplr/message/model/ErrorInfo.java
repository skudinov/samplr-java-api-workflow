package samplr.message.model;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Builder
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@Accessors(fluent = true, chain = true)
public class ErrorInfo {
    private String code;
    private String message;
    private Map<String, String> details;
}
