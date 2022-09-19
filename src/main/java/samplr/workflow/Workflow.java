package samplr.workflow;

import lombok.RequiredArgsConstructor;
import samplr.message.MessageRouter;
import samplr.message.model.ReplyMessage;
import samplr.message.model.RequestContext;
import samplr.message.model.RequestMessage;
import samplr.message.model.RoutingSlip;
import samplr.workflow.model.WorkflowAction;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class Workflow<A extends WorkflowAction> {
    private final MessageRouter router;

    public Optional<ReplyMessage> start(A action, RequestContext context) {
        return router.post(
                RequestMessage.builder()
                        .workflowId(UUID.randomUUID().toString())
                        .context(context)
                        .routingSlip(routingSlip(action))
                        .payload(action)
                    .build()
            );
    }

    protected abstract RoutingSlip routingSlip(A action);

}
