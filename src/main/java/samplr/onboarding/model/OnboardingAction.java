package samplr.onboarding.model;

import lombok.*;
import lombok.experimental.Accessors;
import samplr.workflow.model.WorkflowAction;

import java.util.List;

@Data
@Builder(toBuilder = true)
@ToString
@Accessors(fluent = true, chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingAction implements WorkflowAction {
    private OnboardingApplication application;
    private String validationResponse;
    @Singular
    private List<String> existingPackages;
    private String submitResponse;
}
