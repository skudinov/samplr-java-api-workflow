package samplr.onboarding.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.databind.JsonNode;
import samplr.workflow.model.WorkflowState;

public enum OnboardingState implements WorkflowState {
    VALIDATE_RULES,
    ENRICH_APPLICATION,
    SUBMIT_APPLICATION;

    @JsonCreator(mode = Mode.DELEGATING)
    public static OnboardingState of(JsonNode name) {
        return valueOf(name.get("name").asText());
    }
}
