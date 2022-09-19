package samplr.message.model;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Optional;

@Data
@Builder(toBuilder = true)
@Accessors(fluent = true, chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class RoutingSlip {

    @Singular
    private List<RoutingStep> steps;
    private int currentStepIndex;

    public RoutingStep currentStep() {
        return steps.get(currentStepIndex);
    }

    public RoutingStep firstStep() {
        return steps.get(0);
    }

    public Optional<RoutingStep> nextStep() {
        return currentStepIndex < steps.size() - 1 ? Optional.of(steps.get(currentStepIndex + 1)) : Optional.empty();
    }

    public boolean isCurrentStepLast() {
        return currentStepIndex == steps.size() - 1;
    }

}
