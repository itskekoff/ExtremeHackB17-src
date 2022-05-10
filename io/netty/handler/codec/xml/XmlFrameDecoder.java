package io.netty.handler.codec.xml;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.TooLongFrameException;
import java.util.List;

public class XmlFrameDecoder
extends ByteToMessageDecoder {
    private final int maxFrameLength;

    public XmlFrameDecoder(int maxFrameLength) {
        if (maxFrameLength < 1) {
            throw new IllegalArgumentException("maxFrameLength must be a positive int");
        }
        this.maxFrameLength = maxFrameLength;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in2, List<Object> out) throws Exception {
        boolean openingBracketFound = false;
        boolean atLeastOneXmlElementFound = false;
        boolean inCDATASection = false;
        long openBracketsCount = 0L;
        int length = 0;
        int leadingWhiteSpaceCount = 0;
        int bufferLength = in2.writerIndex();
        if (bufferLength > this.maxFrameLength) {
            in2.skipBytes(in2.readableBytes());
            this.fail(bufferLength);
            return;
        }
        block0: for (int i2 = in2.readerIndex(); i2 < bufferLength; ++i2) {
            byte readByte = in2.getByte(i2);
            if (!openingBracketFound && Character.isWhitespace(readByte)) {
                ++leadingWhiteSpaceCount;
                continue;
            }
            if (!openingBracketFound && readByte != 60) {
                XmlFrameDecoder.fail(ctx);
                in2.skipBytes(in2.readableBytes());
                return;
            }
            if (!inCDATASection && readByte == 60) {
                openingBracketFound = true;
                if (i2 >= bufferLength - 1) continue;
                byte peekAheadByte = in2.getByte(i2 + 1);
                if (peekAheadByte == 47) {
                    for (int peekFurtherAheadIndex = i2 + 2; peekFurtherAheadIndex <= bufferLength - 1; ++peekFurtherAheadIndex) {
                        if (in2.getByte(peekFurtherAheadIndex) != 62) continue;
                        --openBracketsCount;
                        continue block0;
                    }
                    continue;
                }
                if (XmlFrameDecoder.isValidStartCharForXmlElement(peekAheadByte)) {
                    atLeastOneXmlElementFound = true;
                    ++openBracketsCount;
                    continue;
                }
                if (peekAheadByte == 33) {
                    if (XmlFrameDecoder.isCommentBlockStart(in2, i2)) {
                        ++openBracketsCount;
                        continue;
                    }
                    if (!XmlFrameDecoder.isCDATABlockStart(in2, i2)) continue;
                    ++openBracketsCount;
                    inCDATASection = true;
                    continue;
                }
                if (peekAheadByte != 63) continue;
                ++openBracketsCount;
                continue;
            }
            if (!inCDATASection && readByte == 47) {
                if (i2 >= bufferLength - 1 || in2.getByte(i2 + 1) != 62) continue;
                --openBracketsCount;
                continue;
            }
            if (readByte != 62) continue;
            length = i2 + 1;
            if (i2 - 1 > -1) {
                byte peekBehindByte = in2.getByte(i2 - 1);
                if (!inCDATASection) {
                    if (peekBehindByte == 63) {
                        --openBracketsCount;
                    } else if (peekBehindByte == 45 && i2 - 2 > -1 && in2.getByte(i2 - 2) == 45) {
                        --openBracketsCount;
                    }
                } else if (peekBehindByte == 93 && i2 - 2 > -1 && in2.getByte(i2 - 2) == 93) {
                    --openBracketsCount;
                    inCDATASection = false;
                }
            }
            if (atLeastOneXmlElementFound && openBracketsCount == 0L) break;
        }
        int readerIndex = in2.readerIndex();
        int xmlElementLength = length - readerIndex;
        if (openBracketsCount == 0L && xmlElementLength > 0) {
            if (readerIndex + xmlElementLength >= bufferLength) {
                xmlElementLength = in2.readableBytes();
            }
            ByteBuf frame = XmlFrameDecoder.extractFrame(in2, readerIndex + leadingWhiteSpaceCount, xmlElementLength - leadingWhiteSpaceCount);
            in2.skipBytes(xmlElementLength);
            out.add(frame);
        }
    }

    private void fail(long frameLength) {
        if (frameLength > 0L) {
            throw new TooLongFrameException("frame length exceeds " + this.maxFrameLength + ": " + frameLength + " - discarded");
        }
        throw new TooLongFrameException("frame length exceeds " + this.maxFrameLength + " - discarding");
    }

    private static void fail(ChannelHandlerContext ctx) {
        ctx.fireExceptionCaught(new CorruptedFrameException("frame contains content before the xml starts"));
    }

    private static ByteBuf extractFrame(ByteBuf buffer, int index, int length) {
        return buffer.copy(index, length);
    }

    private static boolean isValidStartCharForXmlElement(byte b2) {
        return b2 >= 97 && b2 <= 122 || b2 >= 65 && b2 <= 90 || b2 == 58 || b2 == 95;
    }

    private static boolean isCommentBlockStart(ByteBuf in2, int i2) {
        return i2 < in2.writerIndex() - 3 && in2.getByte(i2 + 2) == 45 && in2.getByte(i2 + 3) == 45;
    }

    private static boolean isCDATABlockStart(ByteBuf in2, int i2) {
        return i2 < in2.writerIndex() - 8 && in2.getByte(i2 + 2) == 91 && in2.getByte(i2 + 3) == 67 && in2.getByte(i2 + 4) == 68 && in2.getByte(i2 + 5) == 65 && in2.getByte(i2 + 6) == 84 && in2.getByte(i2 + 7) == 65 && in2.getByte(i2 + 8) == 91;
    }
}

