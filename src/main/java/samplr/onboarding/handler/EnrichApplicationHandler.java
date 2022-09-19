package samplr.onboarding.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import samplr.message.model.RequestContext;
import samplr.onboarding.model.OnboardingAction;
import samplr.onboarding.model.OnboardingState;
import samplr.workflow.ActionHandler;

@Component
@Slf4j
public class EnrichApplicationHandler extends ActionHandler<OnboardingAction, OnboardingState> {

    public EnrichApplicationHandler() {
        super(OnboardingState.ENRICH_APPLICATION);
    }

    @Override
    protected OnboardingAction handle(OnboardingAction action, RequestContext context) {
        return action.toBuilder()
                .existingPackage("LifeProtection+")
                .existingPackage("NonStopSafeTravel+")
            .build();
    }
}
