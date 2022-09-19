package samplr.message;

import lombok.experimental.UtilityClass;
import samplr.message.model.RequestMessage;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.HashMap;
import java.util.Map;

import static samplr.common.Constants.WORKFLOW_ID;

@UtilityClass
public class MessageUtils {
    private static final String DATA_TYPE_STRING = "String";

    public SendMessageRequest buildRequest(String messageBody, String queueUrl,
                                           Map<String, MessageAttributeValue> attributes) {
        return SendMessageRequest.builder()
                .messageBody(messageBody)
                .queueUrl(queueUrl)
                .messageAttributes(attributes)
                .build();
    }

    public static Map<String, MessageAttributeValue> buildAttributes(RequestMessage requestMessage) {
        final var attributes = new HashMap<>(requestMessage.attributes());
        attributes.put(WORKFLOW_ID, MessageAttributeValue.builder()
                .stringValue(requestMessage.workflowId()).dataType(DATA_TYPE_STRING).build());
        return attributes;
    }
}
