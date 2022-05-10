package io.netty.handler.codec.mqtt;

import io.netty.util.internal.StringUtil;
import java.util.Collections;
import java.util.List;

public final class MqttUnsubscribePayload {
    private final List<String> topics;

    public MqttUnsubscribePayload(List<String> topics) {
        this.topics = Collections.unmodifiableList(topics);
    }

    public List<String> topics() {
        return this.topics;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder(StringUtil.simpleClassName(this)).append('[');
        for (int i2 = 0; i2 < this.topics.size() - 1; ++i2) {
            builder.append("topicName = ").append(this.topics.get(i2)).append(", ");
        }
        builder.append("topicName = ").append(this.topics.get(this.topics.size() - 1)).append(']');
        return builder.toString();
    }
}

