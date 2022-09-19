package samplr.message;

import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

@RequiredArgsConstructor
public class MessageConsumerStarter {
    private final List<MessageConsumer> messageConsumers;

    @PostConstruct
    public void startMessageConsumers() {
        messageConsumers.forEach(MessageConsumer::start);
    }

    @PreDestroy
    public void stopMessageConsumers() {
        messageConsumers.forEach(MessageConsumer::stop);
    }

}
