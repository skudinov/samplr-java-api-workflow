package samplr.onboarding;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import samplr.message.MessageRouter;
import samplr.message.model.MessageQueue;
import samplr.onboarding.OnboardingWorkflow;
import samplr.onboarding.model.OnboardingQueue;

import java.util.List;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Configuration
public class OnboardingConfiguration {
    @Autowired
    private MessageRouter router;

    @Bean
    @Scope(SCOPE_PROTOTYPE)
    public OnboardingWorkflow onboardingWorkflow(boolean replyRequired) {
        return new OnboardingWorkflow(router, replyRequired);
    }

    @Bean
    public List<MessageQueue> onboardingQueues() {
        return List.of(OnboardingQueue.values());
    }
}
