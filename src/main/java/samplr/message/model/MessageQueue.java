package samplr.message.model;

import com.fasterxml.jackson.annotation.*;

import java.util.List;
import java.util.Properties;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
public interface MessageQueue {
    @JsonProperty
    String name();

    @JsonIgnore
    String propertyName();

    @JsonIgnore
    default String url(Properties properties) {
        return String.valueOf(properties.get(propertyName()));
    }

}
