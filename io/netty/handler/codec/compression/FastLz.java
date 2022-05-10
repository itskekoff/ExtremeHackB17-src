package io.netty.handler.codec.compression;

import io.netty.handler.codec.compression.DecompressionException;

final class FastLz {
    private static final int MAX_DISTANCE = 8191;
    private static final int MAX_FARDISTANCE = 73725;
    private static final int HASH_LOG = 13;
    private static final int HASH_SIZE = 8192;
    private static final int HASH_MASK = 8191;
    private static final int MAX_COPY = 32;
    private static final int MAX_LEN = 264;
    private static final int MIN_RECOMENDED_LENGTH_FOR_LEVEL_2 = 65536;
    static final int MAGIC_NUMBER = 4607066;
    static final byte BLOCK_TYPE_NON_COMPRESSED = 0;
    static final byte BLOCK_TYPE_COMPRESSED = 1;
    static final byte BLOCK_WITHOUT_CHECKSUM = 0;
    static final byte BLOCK_WITH_CHECKSUM = 16;
    static final int OPTIONS_OFFSET = 3;
    static final int CHECKSUM_OFFSET = 4;
    static final int MAX_CHUNK_LENGTH = 65535;
    static final int MIN_LENGTH_TO_COMPRESSION = 32;
    static final int LEVEL_AUTO = 0;
    static final int LEVEL_1 = 1;
    static final int LEVEL_2 = 2;

    static int calculateOutputBufferLength(int inputLength) {
        int outputLength = (int)((double)inputLength * 1.06);
        return Math.max(outputLength, 66);
    }

    static int compress(byte[] input, int inOffset, int inLength, byte[] output, int outOffset, int proposedLevel) {
        int hslot;
        int level = proposedLevel == 0 ? (inLength < 65536 ? 1 : 2) : proposedLevel;
        int ip2 = 0;
        int ipBound = ip2 + inLength - 2;
        int ipLimit = ip2 + inLength - 12;
        int op2 = 0;
        int[] htab = new int[8192];
        if (inLength < 4) {
            if (inLength != 0) {
                output[outOffset + op2++] = (byte)(inLength - 1);
                while (ip2 <= ++ipBound) {
                    output[outOffset + op2++] = input[inOffset + ip2++];
                }
                return inLength + 1;
            }
            return 0;
        }
        for (hslot = 0; hslot < 8192; ++hslot) {
            htab[hslot] = ip2;
        }
        int copy = 2;
        output[outOffset + op2++] = 31;
        output[outOffset + op2++] = input[inOffset + ip2++];
        output[outOffset + op2++] = input[inOffset + ip2++];
        while (ip2 < ipLimit) {
            int hval;
            int len;
            long distance;
            int ref;
            block37: {
                int anchor;
                block39: {
                    block38: {
                        ref = 0;
                        distance = 0L;
                        len = 3;
                        anchor = ip2;
                        boolean matchLabel = false;
                        if (level == 2 && input[inOffset + ip2] == input[inOffset + ip2 - 1] && FastLz.readU16(input, inOffset + ip2 - 1) == FastLz.readU16(input, inOffset + ip2 + 1)) {
                            distance = 1L;
                            ip2 += 3;
                            ref = anchor - 1 + 3;
                            matchLabel = true;
                        }
                        if (matchLabel) break block37;
                        hslot = hval = FastLz.hashFunction(input, inOffset + ip2);
                        ref = htab[hval];
                        distance = anchor - ref;
                        htab[hslot] = anchor;
                        if (distance == 0L || (level != 1 ? distance >= 73725L : distance >= 8191L)) break block38;
                        if (input[inOffset + ref++] == input[inOffset + ip2++] && input[inOffset + ref++] == input[inOffset + ip2++] && input[inOffset + ref++] == input[inOffset + ip2++]) break block39;
                    }
                    output[outOffset + op2++] = input[inOffset + anchor++];
                    ip2 = anchor;
                    if (++copy != 32) continue;
                    copy = 0;
                    output[outOffset + op2++] = 31;
                    continue;
                }
                if (level == 2 && distance >= 8191L) {
                    if (input[inOffset + ip2++] != input[inOffset + ref++] || input[inOffset + ip2++] != input[inOffset + ref++]) {
                        output[outOffset + op2++] = input[inOffset + anchor++];
                        ip2 = anchor;
                        if (++copy != 32) continue;
                        copy = 0;
                        output[outOffset + op2++] = 31;
                        continue;
                    }
                    len += 2;
                }
            }
            if (--distance == 0L) {
                byte x2 = input[inOffset + ip2 - 1];
                for (ip2 = anchor + len; ip2 < ipBound && input[inOffset + ref++] == x2; ++ip2) {
                }
            } else if (input[inOffset + ref++] == input[inOffset + ip2++] && input[inOffset + ref++] == input[inOffset + ip2++] && input[inOffset + ref++] == input[inOffset + ip2++] && input[inOffset + ref++] == input[inOffset + ip2++] && input[inOffset + ref++] == input[inOffset + ip2++] && input[inOffset + ref++] == input[inOffset + ip2++] && input[inOffset + ref++] == input[inOffset + ip2++] && input[inOffset + ref++] == input[inOffset + ip2++]) {
                while (ip2 < ipBound && input[inOffset + ref++] == input[inOffset + ip2++]) {
                }
            }
            if (copy != 0) {
                output[outOffset + op2 - copy - 1] = (byte)(copy - 1);
            } else {
                --op2;
            }
            copy = 0;
            if (level == 2) {
                if (distance < 8191L) {
                    if (len < 7) {
                        output[outOffset + op2++] = (byte)((long)(len << 5) + (distance >>> 8));
                        output[outOffset + op2++] = (byte)(distance & 0xFFL);
                    } else {
                        output[outOffset + op2++] = (byte)(224L + (distance >>> 8));
                        len -= 7;
                        while (len >= 255) {
                            output[outOffset + op2++] = -1;
                            len -= 255;
                        }
                        output[outOffset + op2++] = (byte)len;
                        output[outOffset + op2++] = (byte)(distance & 0xFFL);
                    }
                } else if (len < 7) {
                    output[outOffset + op2++] = (byte)((len << 5) + 31);
                    output[outOffset + op2++] = -1;
                    output[outOffset + op2++] = (byte)((distance -= 8191L) >>> 8);
                    output[outOffset + op2++] = (byte)(distance & 0xFFL);
                } else {
                    distance -= 8191L;
                    output[outOffset + op2++] = -1;
                    len -= 7;
                    while (len >= 255) {
                        output[outOffset + op2++] = -1;
                        len -= 255;
                    }
                    output[outOffset + op2++] = (byte)len;
                    output[outOffset + op2++] = -1;
                    output[outOffset + op2++] = (byte)(distance >>> 8);
                    output[outOffset + op2++] = (byte)(distance & 0xFFL);
                }
            } else {
                if (len > 262) {
                    for (len = (ip2 -= 3) - anchor; len > 262; len -= 262) {
                        output[outOffset + op2++] = (byte)(224L + (distance >>> 8));
                        output[outOffset + op2++] = -3;
                        output[outOffset + op2++] = (byte)(distance & 0xFFL);
                    }
                }
                if (len < 7) {
                    output[outOffset + op2++] = (byte)((long)(len << 5) + (distance >>> 8));
                    output[outOffset + op2++] = (byte)(distance & 0xFFL);
                } else {
                    output[outOffset + op2++] = (byte)(224L + (distance >>> 8));
                    output[outOffset + op2++] = (byte)(len - 7);
                    output[outOffset + op2++] = (byte)(distance & 0xFFL);
                }
            }
            hval = FastLz.hashFunction(input, inOffset + ip2);
            htab[hval] = ip2++;
            hval = FastLz.hashFunction(input, inOffset + ip2);
            htab[hval] = ip2++;
            output[outOffset + op2++] = 31;
        }
        while (ip2 <= ++ipBound) {
            output[outOffset + op2++] = input[inOffset + ip2++];
            if (++copy != 32) continue;
            copy = 0;
            output[outOffset + op2++] = 31;
        }
        if (copy != 0) {
            output[outOffset + op2 - copy - 1] = (byte)(copy - 1);
        } else {
            --op2;
        }
        if (level == 2) {
            int n2 = outOffset;
            output[n2] = (byte)(output[n2] | 0x20);
        }
        return op2;
    }

    static int decompress(byte[] input, int inOffset, int inLength, byte[] output, int outOffset, int outLength) {
        int level = (input[inOffset] >> 5) + 1;
        if (level != 1 && level != 2) {
            throw new DecompressionException(String.format("invalid level: %d (expected: %d or %d)", level, 1, 2));
        }
        int ip2 = 0;
        int op2 = 0;
        long ctrl = input[inOffset + ip2++] & 0x1F;
        boolean loop = true;
        do {
            int ref = op2;
            long len = ctrl >> 5;
            long ofs = (ctrl & 0x1FL) << 8;
            if (ctrl >= 32L) {
                int code;
                ref = (int)((long)ref - ofs);
                if (--len == 6L) {
                    if (level == 1) {
                        len += (long)(input[inOffset + ip2++] & 0xFF);
                    } else {
                        do {
                            code = input[inOffset + ip2++] & 0xFF;
                            len += (long)code;
                        } while (code == 255);
                    }
                }
                if (level == 1) {
                    ref -= input[inOffset + ip2++] & 0xFF;
                } else {
                    code = input[inOffset + ip2++] & 0xFF;
                    ref -= code;
                    if (code == 255 && ofs == 7936L) {
                        ofs = (input[inOffset + ip2++] & 0xFF) << 8;
                        ref = (int)((long)op2 - (ofs += (long)(input[inOffset + ip2++] & 0xFF)) - 8191L);
                    }
                }
                if ((long)op2 + len + 3L > (long)outLength) {
                    return 0;
                }
                if (ref - 1 < 0) {
                    return 0;
                }
                if (ip2 < inLength) {
                    ctrl = input[inOffset + ip2++] & 0xFF;
                } else {
                    loop = false;
                }
                if (ref == op2) {
                    byte b2 = output[outOffset + ref - 1];
                    output[outOffset + op2++] = b2;
                    output[outOffset + op2++] = b2;
                    output[outOffset + op2++] = b2;
                    while (len != 0L) {
                        output[outOffset + op2++] = b2;
                        --len;
                    }
                } else {
                    int n2 = op2++;
                    int n3 = --ref;
                    output[outOffset + n2] = output[outOffset + n3];
                    int n4 = op2++;
                    int n5 = ++ref;
                    output[outOffset + n4] = output[outOffset + n5];
                    int n6 = op2++;
                    int n7 = ++ref;
                    ++ref;
                    output[outOffset + n6] = output[outOffset + n7];
                    while (len != 0L) {
                        output[outOffset + op2++] = output[outOffset + ref++];
                        --len;
                    }
                }
            } else {
                if ((long)op2 + ++ctrl > (long)outLength) {
                    return 0;
                }
                if ((long)ip2 + ctrl > (long)inLength) {
                    return 0;
                }
                output[outOffset + op2++] = input[inOffset + ip2++];
                --ctrl;
                while (ctrl != 0L) {
                    output[outOffset + op2++] = input[inOffset + ip2++];
                    --ctrl;
                }
                boolean bl2 = loop = ip2 < inLength;
                if (!loop) continue;
                ctrl = input[inOffset + ip2++] & 0xFF;
            }
        } while (loop);
        return op2;
    }

    private static int hashFunction(byte[] p2, int offset) {
        int v2 = FastLz.readU16(p2, offset);
        v2 ^= FastLz.readU16(p2, offset + 1) ^ v2 >> 3;
        return v2 &= 0x1FFF;
    }

    private static int readU16(byte[] data, int offset) {
        if (offset + 1 >= data.length) {
            return data[offset] & 0xFF;
        }
        return (data[offset + 1] & 0xFF) << 8 | data[offset] & 0xFF;
    }

    private FastLz() {
    }
}

