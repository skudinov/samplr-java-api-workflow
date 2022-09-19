package samplr.message;

import com.amazonaws.services.sqs.util.SQSMessageConsumer;
import com.amazonaws.services.sqs.util.SQSMessageConsumerBuilder;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import samplr.common.ObjectSerializer;
import samplr.message.model.MessageQueue;
import samplr.message.model.RequestMessage;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.Optional;
import java.util.Properties;
import java.util.function.Consumer;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@ToString(onlyExplicitlyIncluded = true)
public class MessageConsumer {
    private final ObjectSerializer serializer;
    private final MessageRouter router;
    @ToString.Include
    private final MessageQueue queue;
    private final SQSMessageConsumer sqsMessageConsumer;
    @ToString.Include
    private final String queueUrl;

    public MessageConsumer(SqsClient client, ObjectSerializer serializer, MessageRouter router,
                           MessageQueue queue, Properties properties) {
        this.serializer = serializer;
        this.router = router;
        this.queue = queue;
        this.queueUrl = queue.url(properties);
        this.sqsMessageConsumer = SQSMessageConsumerBuilder.standard()
                .withAmazonSQS(client)
                .withQueueUrl(queueUrl)
                .withConsumer(buildMessageConsumer())
                .build();
    }


    private Consumer<Message> buildMessageConsumer() {
        return message -> {
            log.info("-2- == Message RECEIVED {}", kv("sqsMessageId", message.messageId()));
            var replyMessage = Optional.ofNullable(message.body())
                    .map(body -> serializer.deserialize(body, RequestMessage.class)
                            .attributes(message.messageAttributes()))
                    .flatMap(router::route)
                    .orElseThrow(() -> new RuntimeException("No reply message produced"));
            log.info("-2- == Message CONSUMED {}", kv("replyMessage", replyMessage));
        };
    }

    public void start() {
        log.info("Starting: {}", kv("consumer", this));
        sqsMessageConsumer.start();
    }

    public void stop() {
        log.info("Stopping: {}", kv("consumer", this));
        sqsMessageConsumer.shutdown();
    }
}
