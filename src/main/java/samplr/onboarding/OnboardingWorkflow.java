package samplr.onboarding;

import samplr.message.MessageRouter;
import samplr.message.model.DeliveryMethod;
import samplr.message.model.RoutingSlip;
import samplr.onboarding.model.OnboardingAction;
import samplr.onboarding.model.OnboardingQueue;
import samplr.onboarding.model.OnboardingState;
import samplr.workflow.Workflow;

import static samplr.message.model.RoutingStep.of;

public class OnboardingWorkflow extends Workflow<OnboardingAction> {
    private boolean replyRequired;

    public OnboardingWorkflow(MessageRouter router, boolean replyRequired) {
        super(router);
        this.replyRequired = replyRequired;
    }

    @Override
    protected RoutingSlip routingSlip(OnboardingAction action) {
        return RoutingSlip.builder()
                .step(of(replyRequired? DeliveryMethod.REQUEST_WAIT_REPLY : DeliveryMethod.POST,
                                              OnboardingState.VALIDATE_RULES, OnboardingQueue.INBOUND))
                .step(of(DeliveryMethod.POST, OnboardingState.ENRICH_APPLICATION, OnboardingQueue.INBOUND))
                .step(of(DeliveryMethod.POST, OnboardingState.SUBMIT_APPLICATION, OnboardingQueue.INBOUND))
            .build();
    }

}
