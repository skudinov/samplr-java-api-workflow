package samplr.workflow;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import samplr.message.model.ReplyMessage;
import samplr.message.model.RequestContext;
import samplr.message.model.RequestMessage;
import samplr.workflow.model.WorkflowAction;
import samplr.workflow.model.WorkflowState;

import static java.net.HttpURLConnection.HTTP_OK;
import static net.logstash.logback.argument.StructuredArguments.kv;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
@Slf4j
@ToString(onlyExplicitlyIncluded = true)
public abstract class ActionHandler<A extends WorkflowAction, S extends WorkflowState> {
    @ToString.Include
    private final S workflowState;

    public ReplyMessage receive(RequestMessage request) {
        var context = request.context();
        var action = request.payload();
        log.info("-3- [*] Action HANDLED {} {}", kv("handler", this), kv("action", action));
        return ReplyMessage.builder()
                .status(HTTP_OK)
                .payload(handle((A) action, context))
                .build();
    }

    protected abstract A handle(A action, RequestContext context);
}
