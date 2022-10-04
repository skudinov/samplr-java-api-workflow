package samplr.message;

import com.amazonaws.services.sqs.AmazonSQSRequester;
import com.amazonaws.services.sqs.AmazonSQSResponder;
import com.amazonaws.services.sqs.MessageContent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import samplr.common.ObjectSerializer;
import samplr.message.model.ReplyMessage;
import samplr.message.model.RequestMessage;
import samplr.message.model.RoutingStep;
import samplr.workflow.ActionHandler;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static com.amazonaws.services.sqs.util.Constants.RESPONSE_QUEUE_URL_ATTRIBUTE_NAME;
import static java.lang.String.format;
import static net.logstash.logback.argument.StructuredArguments.kv;
import static samplr.common.UnsafeFunction.unsafe;
import static samplr.message.MessageUtils.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageRouter {
    private final SqsClient client;
    private final AmazonSQSRequester requester;
    private final AmazonSQSResponder responder;
    private final ObjectSerializer serializer;
    private final List<ActionHandler> handlers;
    @Qualifier("workflowProperties")
    private final Properties properties;

    public Optional<ReplyMessage> post(RequestMessage message) {
        final var step = message.routingSlip().currentStep();
        final var queue = step.queue();
        return Optional.of(serializer.serialize(message))
                .map(requestBody -> buildRequest(requestBody, queue.url(properties), buildAttributes(message)))
                .flatMap(request -> send(request, step));
    }

    private Optional<ReplyMessage> send(SendMessageRequest sqsRequest, RoutingStep<?> step) {
        log.info("-1- >> Request POSTING {}", kv("workflowStep", step));
        switch (step.method()) {
            case REQUEST_WAIT_REPLY:
                return Optional.of(sqsRequest)
                        .map(unsafe(r -> {
                            log.info("-1- >> [!] Request POSTED, REPLY_WAITING {}", kv("sqsRequest", r));
                            return requester.sendMessageAndGetResponse(r, 2, TimeUnit.MINUTES);
                        }))
                        .map(Message::body)
                        .map(replyBody -> {
                            ReplyMessage replyMessage = serializer.deserialize(replyBody, ReplyMessage.class);
                            log.info("-1- << [!] Request REPLY_RECEIVED {}", kv("replyMessage", replyMessage));
                            return replyMessage;
                        });
            case REPLY_THEN_POST:
            case POST:
                return Optional.of(sqsRequest)
                        .map(client::sendMessage)
                        .map(sqsResponse -> {
                            log.info("-1- << Request POSTED, no REPLY expected {}", kv("sqsResponse", sqsResponse));
                            return ReplyMessage.accepted();
                        });
            default:
                throw new UnsupportedOperationException(format("Delivery method %s not supported", step.method()));
        }
    }

    Optional<ReplyMessage> route(RequestMessage requestMessage) {
        log.info("-2- Request ROUTING {}", kv("requestMessageId", requestMessage.id()));
        final var isLastStep = requestMessage.routingSlip().isCurrentStepLast();
        // handle request
        final var replyMessage = handle(requestMessage);
        // reply if needed
        if (requestMessage.isReplyMessageRequested() && isLastStep) {
            reply(replyMessage);
        }
        // post next if any and handling is successful
        return !replyMessage.isSuccessful() || isLastStep ?
                Optional.of(replyMessage) :
                replyMessage.toNextRequest()
                        .flatMap(this::post);
    }

    private void reply(ReplyMessage replyMessage) {
        var attributes = replyMessage.request().attributes();
        log.info("-2- Reply POSTED {}", kv("queueUrl", attributes.get(RESPONSE_QUEUE_URL_ATTRIBUTE_NAME)),
                kv("replyMessage", replyMessage));
        var requestContent = new MessageContent("", attributes);
        var replyContent = new MessageContent(serializer.serialize(replyMessage));
        responder.sendResponseMessage(requestContent, replyContent);
    }

    ReplyMessage handle(RequestMessage requestMessage) {
        var state = requestMessage.routingSlip().currentStep().state();
        return handlers.stream()
                .filter(handler -> handler.workflowState().equals(state))
                .findFirst()
                .map(handler -> handler.receive(requestMessage))
                .map(replyMessage -> replyMessage.request(requestMessage))
                .orElseThrow(() -> new RuntimeException("Handler not found, state: " + state));
    }


}
