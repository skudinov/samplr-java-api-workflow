package samplr.message.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import samplr.workflow.model.WorkflowAction;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static com.amazonaws.services.sqs.util.Constants.RESPONSE_QUEUE_URL_ATTRIBUTE_NAME;

@Data
@Builder(toBuilder = true)
@Accessors(fluent = true, chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class RequestMessage {
    private final int version = 1;
    @Builder.Default
    private String id = UUID.randomUUID().toString();
    private String workflowId;
    private RequestContext context;
    private RoutingSlip routingSlip;
    private WorkflowAction payload;
    @JsonIgnore
    @Builder.Default
    private Map<String, MessageAttributeValue> attributes = Collections.emptyMap();

    public boolean isReplyMessageRequested() {
        return attributes.containsKey(RESPONSE_QUEUE_URL_ATTRIBUTE_NAME);
    }

}
