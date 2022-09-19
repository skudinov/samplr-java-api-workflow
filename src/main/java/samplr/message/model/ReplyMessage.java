package samplr.message.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import samplr.workflow.model.WorkflowAction;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Data
@Builder
@Accessors(fluent = true, chain = true)
@AllArgsConstructor
@RequiredArgsConstructor
public class ReplyMessage {
    private final int version = 1;
    private int status;
    private ErrorInfo error;
    private WorkflowAction payload;
    private RequestMessage request;

    public static ReplyMessage accepted() {
        return ReplyMessage.builder().status(201).build();
    }

    @JsonIgnore
    public boolean isSuccessful() {
        return status <= 399;
    }

    public Optional<RequestMessage> toNextRequest() {
        return Optional.of(request.routingSlip())
                .flatMap(routingSlip -> routingSlip.nextStep()
                        .map(nextStep -> routingSlip.toBuilder()
                                .currentStepIndex(routingSlip.steps().indexOf(nextStep))
                                .build()))
                .map(routingSlip -> request.toBuilder()
                        .id(UUID.randomUUID().toString())
                        .attributes(Map.copyOf(request.attributes()))
                        .routingSlip(routingSlip)
                        .payload(payload)
                        .build());
    }

    public ResponseEntity<ReplyMessage> toResponseEntity() {
        return ResponseEntity.status(status)
            .contentType(MediaType.APPLICATION_JSON)
            .body(this);
    }
}
