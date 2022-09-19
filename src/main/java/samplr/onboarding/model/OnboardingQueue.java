package samplr.onboarding.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import samplr.message.model.MessageQueue;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public enum OnboardingQueue implements MessageQueue {
    INBOUND("inbound_queue_url");

    private final String propertyName;
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static OnboardingQueue of(JsonNode name) {
        return valueOf(name.get("name").asText());
    }

}
