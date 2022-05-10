package io.netty.handler.codec.mqtt;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.mqtt.MqttCodecUtil;
import io.netty.handler.codec.mqtt.MqttConnAckMessage;
import io.netty.handler.codec.mqtt.MqttConnectMessage;
import io.netty.handler.codec.mqtt.MqttConnectPayload;
import io.netty.handler.codec.mqtt.MqttConnectVariableHeader;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttIdentifierRejectedException;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageIdVariableHeader;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;
import io.netty.handler.codec.mqtt.MqttSubAckMessage;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import io.netty.handler.codec.mqtt.MqttSubscribePayload;
import io.netty.handler.codec.mqtt.MqttTopicSubscription;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;
import io.netty.handler.codec.mqtt.MqttUnsubscribePayload;
import io.netty.handler.codec.mqtt.MqttVersion;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.EmptyArrays;
import java.util.List;

@ChannelHandler.Sharable
public final class MqttEncoder
extends MessageToMessageEncoder<MqttMessage> {
    public static final MqttEncoder INSTANCE = new MqttEncoder();

    private MqttEncoder() {
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, MqttMessage msg, List<Object> out) throws Exception {
        out.add(MqttEncoder.doEncode(ctx.alloc(), msg));
    }

    static ByteBuf doEncode(ByteBufAllocator byteBufAllocator, MqttMessage message) {
        switch (message.fixedHeader().messageType()) {
            case CONNECT: {
                return MqttEncoder.encodeConnectMessage(byteBufAllocator, (MqttConnectMessage)message);
            }
            case CONNACK: {
                return MqttEncoder.encodeConnAckMessage(byteBufAllocator, (MqttConnAckMessage)message);
            }
            case PUBLISH: {
                return MqttEncoder.encodePublishMessage(byteBufAllocator, (MqttPublishMessage)message);
            }
            case SUBSCRIBE: {
                return MqttEncoder.encodeSubscribeMessage(byteBufAllocator, (MqttSubscribeMessage)message);
            }
            case UNSUBSCRIBE: {
                return MqttEncoder.encodeUnsubscribeMessage(byteBufAllocator, (MqttUnsubscribeMessage)message);
            }
            case SUBACK: {
                return MqttEncoder.encodeSubAckMessage(byteBufAllocator, (MqttSubAckMessage)message);
            }
            case UNSUBACK: 
            case PUBACK: 
            case PUBREC: 
            case PUBREL: 
            case PUBCOMP: {
                return MqttEncoder.encodeMessageWithOnlySingleByteFixedHeaderAndMessageId(byteBufAllocator, message);
            }
            case PINGREQ: 
            case PINGRESP: 
            case DISCONNECT: {
                return MqttEncoder.encodeMessageWithOnlySingleByteFixedHeader(byteBufAllocator, message);
            }
        }
        throw new IllegalArgumentException("Unknown message type: " + message.fixedHeader().messageType().value());
    }

    private static ByteBuf encodeConnectMessage(ByteBufAllocator byteBufAllocator, MqttConnectMessage message) {
        String password;
        byte[] passwordBytes;
        String userName;
        byte[] userNameBytes;
        byte[] willMessageBytes;
        String clientIdentifier;
        int payloadBufferSize = 0;
        MqttFixedHeader mqttFixedHeader = message.fixedHeader();
        MqttConnectVariableHeader variableHeader = message.variableHeader();
        MqttConnectPayload payload = message.payload();
        MqttVersion mqttVersion = MqttVersion.fromProtocolNameAndLevel(variableHeader.name(), (byte)variableHeader.version());
        if (!MqttCodecUtil.isValidClientId(mqttVersion, clientIdentifier = payload.clientIdentifier())) {
            throw new MqttIdentifierRejectedException("invalid clientIdentifier: " + clientIdentifier);
        }
        byte[] clientIdentifierBytes = MqttEncoder.encodeStringUtf8(clientIdentifier);
        payloadBufferSize += 2 + clientIdentifierBytes.length;
        String willTopic = payload.willTopic();
        byte[] willTopicBytes = willTopic != null ? MqttEncoder.encodeStringUtf8(willTopic) : EmptyArrays.EMPTY_BYTES;
        String willMessage = payload.willMessage();
        byte[] arrby = willMessageBytes = willMessage != null ? MqttEncoder.encodeStringUtf8(willMessage) : EmptyArrays.EMPTY_BYTES;
        if (variableHeader.isWillFlag()) {
            payloadBufferSize += 2 + willTopicBytes.length;
            payloadBufferSize += 2 + willMessageBytes.length;
        }
        byte[] arrby2 = userNameBytes = (userName = payload.userName()) != null ? MqttEncoder.encodeStringUtf8(userName) : EmptyArrays.EMPTY_BYTES;
        if (variableHeader.hasUserName()) {
            payloadBufferSize += 2 + userNameBytes.length;
        }
        byte[] arrby3 = passwordBytes = (password = payload.password()) != null ? MqttEncoder.encodeStringUtf8(password) : EmptyArrays.EMPTY_BYTES;
        if (variableHeader.hasPassword()) {
            payloadBufferSize += 2 + passwordBytes.length;
        }
        byte[] protocolNameBytes = mqttVersion.protocolNameBytes();
        int variableHeaderBufferSize = 2 + protocolNameBytes.length + 4;
        int variablePartSize = variableHeaderBufferSize + payloadBufferSize;
        int fixedHeaderBufferSize = 1 + MqttEncoder.getVariableLengthInt(variablePartSize);
        ByteBuf buf2 = byteBufAllocator.buffer(fixedHeaderBufferSize + variablePartSize);
        buf2.writeByte(MqttEncoder.getFixedHeaderByte1(mqttFixedHeader));
        MqttEncoder.writeVariableLengthInt(buf2, variablePartSize);
        buf2.writeShort(protocolNameBytes.length);
        buf2.writeBytes(protocolNameBytes);
        buf2.writeByte(variableHeader.version());
        buf2.writeByte(MqttEncoder.getConnVariableHeaderFlag(variableHeader));
        buf2.writeShort(variableHeader.keepAliveTimeSeconds());
        buf2.writeShort(clientIdentifierBytes.length);
        buf2.writeBytes(clientIdentifierBytes, 0, clientIdentifierBytes.length);
        if (variableHeader.isWillFlag()) {
            buf2.writeShort(willTopicBytes.length);
            buf2.writeBytes(willTopicBytes, 0, willTopicBytes.length);
            buf2.writeShort(willMessageBytes.length);
            buf2.writeBytes(willMessageBytes, 0, willMessageBytes.length);
        }
        if (variableHeader.hasUserName()) {
            buf2.writeShort(userNameBytes.length);
            buf2.writeBytes(userNameBytes, 0, userNameBytes.length);
        }
        if (variableHeader.hasPassword()) {
            buf2.writeShort(passwordBytes.length);
            buf2.writeBytes(passwordBytes, 0, passwordBytes.length);
        }
        return buf2;
    }

    private static int getConnVariableHeaderFlag(MqttConnectVariableHeader variableHeader) {
        int flagByte = 0;
        if (variableHeader.hasUserName()) {
            flagByte |= 0x80;
        }
        if (variableHeader.hasPassword()) {
            flagByte |= 0x40;
        }
        if (variableHeader.isWillRetain()) {
            flagByte |= 0x20;
        }
        flagByte |= (variableHeader.willQos() & 3) << 3;
        if (variableHeader.isWillFlag()) {
            flagByte |= 4;
        }
        if (variableHeader.isCleanSession()) {
            flagByte |= 2;
        }
        return flagByte;
    }

    private static ByteBuf encodeConnAckMessage(ByteBufAllocator byteBufAllocator, MqttConnAckMessage message) {
        ByteBuf buf2 = byteBufAllocator.buffer(4);
        buf2.writeByte(MqttEncoder.getFixedHeaderByte1(message.fixedHeader()));
        buf2.writeByte(2);
        buf2.writeByte(message.variableHeader().isSessionPresent() ? 1 : 0);
        buf2.writeByte(message.variableHeader().connectReturnCode().byteValue());
        return buf2;
    }

    private static ByteBuf encodeSubscribeMessage(ByteBufAllocator byteBufAllocator, MqttSubscribeMessage message) {
        int variableHeaderBufferSize = 2;
        int payloadBufferSize = 0;
        MqttFixedHeader mqttFixedHeader = message.fixedHeader();
        MqttMessageIdVariableHeader variableHeader = message.variableHeader();
        MqttSubscribePayload payload = message.payload();
        for (MqttTopicSubscription topic : payload.topicSubscriptions()) {
            String topicName = topic.topicName();
            byte[] topicNameBytes = MqttEncoder.encodeStringUtf8(topicName);
            payloadBufferSize += 2 + topicNameBytes.length;
            ++payloadBufferSize;
        }
        int variablePartSize = variableHeaderBufferSize + payloadBufferSize;
        int fixedHeaderBufferSize = 1 + MqttEncoder.getVariableLengthInt(variablePartSize);
        ByteBuf buf2 = byteBufAllocator.buffer(fixedHeaderBufferSize + variablePartSize);
        buf2.writeByte(MqttEncoder.getFixedHeaderByte1(mqttFixedHeader));
        MqttEncoder.writeVariableLengthInt(buf2, variablePartSize);
        int messageId = variableHeader.messageId();
        buf2.writeShort(messageId);
        for (MqttTopicSubscription topic : payload.topicSubscriptions()) {
            String topicName = topic.topicName();
            byte[] topicNameBytes = MqttEncoder.encodeStringUtf8(topicName);
            buf2.writeShort(topicNameBytes.length);
            buf2.writeBytes(topicNameBytes, 0, topicNameBytes.length);
            buf2.writeByte(topic.qualityOfService().value());
        }
        return buf2;
    }

    private static ByteBuf encodeUnsubscribeMessage(ByteBufAllocator byteBufAllocator, MqttUnsubscribeMessage message) {
        int variableHeaderBufferSize = 2;
        int payloadBufferSize = 0;
        MqttFixedHeader mqttFixedHeader = message.fixedHeader();
        MqttMessageIdVariableHeader variableHeader = message.variableHeader();
        MqttUnsubscribePayload payload = message.payload();
        for (String topicName : payload.topics()) {
            byte[] topicNameBytes = MqttEncoder.encodeStringUtf8(topicName);
            payloadBufferSize += 2 + topicNameBytes.length;
        }
        int variablePartSize = variableHeaderBufferSize + payloadBufferSize;
        int fixedHeaderBufferSize = 1 + MqttEncoder.getVariableLengthInt(variablePartSize);
        ByteBuf buf2 = byteBufAllocator.buffer(fixedHeaderBufferSize + variablePartSize);
        buf2.writeByte(MqttEncoder.getFixedHeaderByte1(mqttFixedHeader));
        MqttEncoder.writeVariableLengthInt(buf2, variablePartSize);
        int messageId = variableHeader.messageId();
        buf2.writeShort(messageId);
        for (String topicName : payload.topics()) {
            byte[] topicNameBytes = MqttEncoder.encodeStringUtf8(topicName);
            buf2.writeShort(topicNameBytes.length);
            buf2.writeBytes(topicNameBytes, 0, topicNameBytes.length);
        }
        return buf2;
    }

    private static ByteBuf encodeSubAckMessage(ByteBufAllocator byteBufAllocator, MqttSubAckMessage message) {
        int variableHeaderBufferSize = 2;
        int payloadBufferSize = message.payload().grantedQoSLevels().size();
        int variablePartSize = variableHeaderBufferSize + payloadBufferSize;
        int fixedHeaderBufferSize = 1 + MqttEncoder.getVariableLengthInt(variablePartSize);
        ByteBuf buf2 = byteBufAllocator.buffer(fixedHeaderBufferSize + variablePartSize);
        buf2.writeByte(MqttEncoder.getFixedHeaderByte1(message.fixedHeader()));
        MqttEncoder.writeVariableLengthInt(buf2, variablePartSize);
        buf2.writeShort(message.variableHeader().messageId());
        for (int qos : message.payload().grantedQoSLevels()) {
            buf2.writeByte(qos);
        }
        return buf2;
    }

    private static ByteBuf encodePublishMessage(ByteBufAllocator byteBufAllocator, MqttPublishMessage message) {
        MqttFixedHeader mqttFixedHeader = message.fixedHeader();
        MqttPublishVariableHeader variableHeader = message.variableHeader();
        ByteBuf payload = message.payload().duplicate();
        String topicName = variableHeader.topicName();
        byte[] topicNameBytes = MqttEncoder.encodeStringUtf8(topicName);
        int variableHeaderBufferSize = 2 + topicNameBytes.length + (mqttFixedHeader.qosLevel().value() > 0 ? 2 : 0);
        int payloadBufferSize = payload.readableBytes();
        int variablePartSize = variableHeaderBufferSize + payloadBufferSize;
        int fixedHeaderBufferSize = 1 + MqttEncoder.getVariableLengthInt(variablePartSize);
        ByteBuf buf2 = byteBufAllocator.buffer(fixedHeaderBufferSize + variablePartSize);
        buf2.writeByte(MqttEncoder.getFixedHeaderByte1(mqttFixedHeader));
        MqttEncoder.writeVariableLengthInt(buf2, variablePartSize);
        buf2.writeShort(topicNameBytes.length);
        buf2.writeBytes(topicNameBytes);
        if (mqttFixedHeader.qosLevel().value() > 0) {
            buf2.writeShort(variableHeader.messageId());
        }
        buf2.writeBytes(payload);
        return buf2;
    }

    private static ByteBuf encodeMessageWithOnlySingleByteFixedHeaderAndMessageId(ByteBufAllocator byteBufAllocator, MqttMessage message) {
        MqttFixedHeader mqttFixedHeader = message.fixedHeader();
        MqttMessageIdVariableHeader variableHeader = (MqttMessageIdVariableHeader)message.variableHeader();
        int msgId = variableHeader.messageId();
        int variableHeaderBufferSize = 2;
        int fixedHeaderBufferSize = 1 + MqttEncoder.getVariableLengthInt(variableHeaderBufferSize);
        ByteBuf buf2 = byteBufAllocator.buffer(fixedHeaderBufferSize + variableHeaderBufferSize);
        buf2.writeByte(MqttEncoder.getFixedHeaderByte1(mqttFixedHeader));
        MqttEncoder.writeVariableLengthInt(buf2, variableHeaderBufferSize);
        buf2.writeShort(msgId);
        return buf2;
    }

    private static ByteBuf encodeMessageWithOnlySingleByteFixedHeader(ByteBufAllocator byteBufAllocator, MqttMessage message) {
        MqttFixedHeader mqttFixedHeader = message.fixedHeader();
        ByteBuf buf2 = byteBufAllocator.buffer(2);
        buf2.writeByte(MqttEncoder.getFixedHeaderByte1(mqttFixedHeader));
        buf2.writeByte(0);
        return buf2;
    }

    private static int getFixedHeaderByte1(MqttFixedHeader header) {
        int ret = 0;
        ret |= header.messageType().value() << 4;
        if (header.isDup()) {
            ret |= 8;
        }
        ret |= header.qosLevel().value() << 1;
        if (header.isRetain()) {
            ret |= 1;
        }
        return ret;
    }

    private static void writeVariableLengthInt(ByteBuf buf2, int num) {
        do {
            int digit = num % 128;
            if ((num /= 128) > 0) {
                digit |= 0x80;
            }
            buf2.writeByte(digit);
        } while (num > 0);
    }

    private static int getVariableLengthInt(int num) {
        int count = 0;
        do {
            ++count;
        } while ((num /= 128) > 0);
        return count;
    }

    private static byte[] encodeStringUtf8(String s2) {
        return s2.getBytes(CharsetUtil.UTF_8);
    }
}

