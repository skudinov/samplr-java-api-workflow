package samplr.message.model;

import lombok.*;
import lombok.experimental.Accessors;
import samplr.workflow.model.WorkflowState;

@Data
@Builder
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@Accessors(fluent = true, chain = true)
public class RoutingStep<S extends WorkflowState> {
    private DeliveryMethod method;
    private S state;
    private MessageQueue queue;

    public static <S extends WorkflowState> RoutingStep<S> of(DeliveryMethod method, S state, MessageQueue queue) {
        return new RoutingStep<>(method, state, queue);
    }
}
