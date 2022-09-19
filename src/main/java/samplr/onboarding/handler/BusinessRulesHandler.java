package samplr.onboarding.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import samplr.message.model.RequestContext;
import samplr.onboarding.model.OnboardingAction;
import samplr.onboarding.model.OnboardingState;
import samplr.workflow.ActionHandler;

@Component
@Slf4j
public class BusinessRulesHandler extends ActionHandler<OnboardingAction, OnboardingState> {

    public BusinessRulesHandler() {
        super(OnboardingState.VALIDATE_RULES);
    }

    @Override
    protected OnboardingAction handle(OnboardingAction action, RequestContext context) {
        return action.toBuilder()
                .validationResponse("Rules validated for user: " + context.userId())
                .build();
    }
}
