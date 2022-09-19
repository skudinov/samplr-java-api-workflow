package samplr.workflow;

import com.amazonaws.services.sqs.AmazonSQSRequester;
import com.amazonaws.services.sqs.AmazonSQSRequesterClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSResponder;
import com.amazonaws.services.sqs.AmazonSQSResponderClientBuilder;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import samplr.common.ObjectSerializer;
import samplr.message.MessageConsumer;
import samplr.message.MessageConsumerStarter;
import samplr.message.MessageRouter;
import samplr.message.model.MessageQueue;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Configuration
public class WorkflowConfiguration {
    @ConfigurationProperties
    @Bean
    public Properties workflowProperties() {
        return new Properties();
    }

    @Bean
    public MessageConsumerStarter messageConsumerStarter(List<MessageQueue> queues,
                                                         SqsClient client,
                                                         MessageRouter router,
                                                         Properties workflowProperties,
                                                         ObjectSerializer serializer,
                                                         ObjectProvider<MessageConsumer> consumerProvider) {
        return new MessageConsumerStarter(
                queues.stream()
                    .distinct()
                    .map(queue -> new MessageConsumer(client, serializer, router, queue, workflowProperties))
                    .collect(Collectors.toList())
            );
    }

    @Bean
    public ObjectMapper objectMapper() {
        var mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        mapper.setSerializationInclusion(Include.NON_NULL);
        return mapper;
    }

    @Bean
    public SqsClient sqsClient() {
        return SqsClient.builder().build();
    }

    @Bean(destroyMethod = "shutdown")
    public AmazonSQSRequester sqsRequester(SqsClient client) {
        return AmazonSQSRequesterClientBuilder.standard()
                .withAmazonSQS(sqsClient())
                .build();
    }

    @Bean(destroyMethod = "shutdown")
    public AmazonSQSResponder sqsResponder(SqsClient sqsClient) {
        return AmazonSQSResponderClientBuilder.standard()
                .withAmazonSQS(sqsClient)
                .build();
    }

}
