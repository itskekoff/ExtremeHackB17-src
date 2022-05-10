package io.netty.handler.codec.stomp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.AsciiHeadersEncoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.stomp.LastStompContentSubframe;
import io.netty.handler.codec.stomp.StompContentSubframe;
import io.netty.handler.codec.stomp.StompFrame;
import io.netty.handler.codec.stomp.StompHeadersSubframe;
import io.netty.handler.codec.stomp.StompSubframe;
import io.netty.util.CharsetUtil;
import java.util.List;
import java.util.Map;

public class StompSubframeEncoder
extends MessageToMessageEncoder<StompSubframe> {
    @Override
    protected void encode(ChannelHandlerContext ctx, StompSubframe msg, List<Object> out) throws Exception {
        if (msg instanceof StompFrame) {
            StompFrame frame = (StompFrame)msg;
            ByteBuf frameBuf = StompSubframeEncoder.encodeFrame(frame, ctx);
            out.add(frameBuf);
            ByteBuf contentBuf = StompSubframeEncoder.encodeContent(frame, ctx);
            out.add(contentBuf);
        } else if (msg instanceof StompHeadersSubframe) {
            StompHeadersSubframe frame = (StompHeadersSubframe)msg;
            ByteBuf buf2 = StompSubframeEncoder.encodeFrame(frame, ctx);
            out.add(buf2);
        } else if (msg instanceof StompContentSubframe) {
            StompContentSubframe stompContentSubframe = (StompContentSubframe)msg;
            ByteBuf buf3 = StompSubframeEncoder.encodeContent(stompContentSubframe, ctx);
            out.add(buf3);
        }
    }

    private static ByteBuf encodeContent(StompContentSubframe content, ChannelHandlerContext ctx) {
        if (content instanceof LastStompContentSubframe) {
            ByteBuf buf2 = ctx.alloc().buffer(content.content().readableBytes() + 1);
            buf2.writeBytes(content.content());
            buf2.writeByte(0);
            return buf2;
        }
        return content.content().retain();
    }

    private static ByteBuf encodeFrame(StompHeadersSubframe frame, ChannelHandlerContext ctx) {
        ByteBuf buf2 = ctx.alloc().buffer();
        buf2.writeBytes(frame.command().toString().getBytes(CharsetUtil.US_ASCII));
        buf2.writeByte(10);
        AsciiHeadersEncoder headersEncoder = new AsciiHeadersEncoder(buf2, AsciiHeadersEncoder.SeparatorType.COLON, AsciiHeadersEncoder.NewlineType.LF);
        for (Map.Entry<CharSequence, CharSequence> entry : frame.headers()) {
            headersEncoder.encode(entry);
        }
        buf2.writeByte(10);
        return buf2;
    }
}

