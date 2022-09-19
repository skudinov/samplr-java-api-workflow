package samplr.resource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import samplr.message.model.ReplyMessage;
import samplr.message.model.RequestContext;
import samplr.onboarding.OnboardingWorkflow;
import samplr.onboarding.model.OnboardingAction;
import samplr.onboarding.model.OnboardingApplication;

import static net.logstash.logback.argument.StructuredArguments.kv;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OnboardingApiController {
    private final ObjectProvider<OnboardingWorkflow> workflowProvider;

    @PostMapping("/applications")
    public ResponseEntity<ReplyMessage> submitApplication(@RequestBody OnboardingApplication application,
                                                          @RequestParam(required = false) String projection,
                                                          @RequestHeader("User-Id") String userId) {
        var replyRequired = "reply".equalsIgnoreCase(projection);
        log.info("*** API Request {}", kv("replyRequired", replyRequired));
        log.info("-0- ### Workflow START {}", kv("application", application));
        var workflow = workflowProvider.getObject(replyRequired);
        return workflow.start(
                        OnboardingAction.builder()
                                .application(application)
                                .build(),
                        RequestContext.builder()
                                .userId(userId)
                                .build())
                .map(replyMessage -> {
                    log.info("-0- ### Workflow END {}", kv("replyMessage", replyMessage));
                    return replyMessage.toResponseEntity();
                })
                .orElseThrow();
    }
}
