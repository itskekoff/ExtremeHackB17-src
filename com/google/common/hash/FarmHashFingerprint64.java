package com.google.common.hash;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.hash.AbstractNonStreamingHashFunction;
import com.google.common.hash.HashCode;
import com.google.common.hash.LittleEndianByteArray;

final class FarmHashFingerprint64
extends AbstractNonStreamingHashFunction {
    private static final long K0 = -4348849565147123417L;
    private static final long K1 = -5435081209227447693L;
    private static final long K2 = -7286425919675154353L;

    FarmHashFingerprint64() {
    }

    @Override
    public HashCode hashBytes(byte[] input, int off, int len) {
        Preconditions.checkPositionIndexes(off, off + len, input.length);
        return HashCode.fromLong(FarmHashFingerprint64.fingerprint(input, off, len));
    }

    @Override
    public int bits() {
        return 64;
    }

    public String toString() {
        return "Hashing.farmHashFingerprint64()";
    }

    @VisibleForTesting
    static long fingerprint(byte[] bytes, int offset, int length) {
        if (length <= 32) {
            if (length <= 16) {
                return FarmHashFingerprint64.hashLength0to16(bytes, offset, length);
            }
            return FarmHashFingerprint64.hashLength17to32(bytes, offset, length);
        }
        if (length <= 64) {
            return FarmHashFingerprint64.hashLength33To64(bytes, offset, length);
        }
        return FarmHashFingerprint64.hashLength65Plus(bytes, offset, length);
    }

    private static long shiftMix(long val) {
        return val ^ val >>> 47;
    }

    private static long hashLength16(long u2, long v2, long mul) {
        long a2 = (u2 ^ v2) * mul;
        a2 ^= a2 >>> 47;
        long b2 = (v2 ^ a2) * mul;
        b2 ^= b2 >>> 47;
        return b2 *= mul;
    }

    private static void weakHashLength32WithSeeds(byte[] bytes, int offset, long seedA, long seedB, long[] output) {
        long part1 = LittleEndianByteArray.load64(bytes, offset);
        long part2 = LittleEndianByteArray.load64(bytes, offset + 8);
        long part3 = LittleEndianByteArray.load64(bytes, offset + 16);
        long part4 = LittleEndianByteArray.load64(bytes, offset + 24);
        seedB = Long.rotateRight(seedB + (seedA += part1) + part4, 21);
        long c2 = seedA;
        seedA += part2;
        output[0] = seedA + part4;
        output[1] = (seedB += Long.rotateRight(seedA += part3, 44)) + c2;
    }

    private static long hashLength0to16(byte[] bytes, int offset, int length) {
        if (length >= 8) {
            long mul = -7286425919675154353L + (long)(length * 2);
            long a2 = LittleEndianByteArray.load64(bytes, offset) + -7286425919675154353L;
            long b2 = LittleEndianByteArray.load64(bytes, offset + length - 8);
            long c2 = Long.rotateRight(b2, 37) * mul + a2;
            long d2 = (Long.rotateRight(a2, 25) + b2) * mul;
            return FarmHashFingerprint64.hashLength16(c2, d2, mul);
        }
        if (length >= 4) {
            long mul = -7286425919675154353L + (long)(length * 2);
            long a3 = (long)LittleEndianByteArray.load32(bytes, offset) & 0xFFFFFFFFL;
            return FarmHashFingerprint64.hashLength16((long)length + (a3 << 3), (long)LittleEndianByteArray.load32(bytes, offset + length - 4) & 0xFFFFFFFFL, mul);
        }
        if (length > 0) {
            byte a4 = bytes[offset];
            byte b3 = bytes[offset + (length >> 1)];
            byte c3 = bytes[offset + (length - 1)];
            int y2 = (a4 & 0xFF) + ((b3 & 0xFF) << 8);
            int z2 = length + ((c3 & 0xFF) << 2);
            return FarmHashFingerprint64.shiftMix((long)y2 * -7286425919675154353L ^ (long)z2 * -4348849565147123417L) * -7286425919675154353L;
        }
        return -7286425919675154353L;
    }

    private static long hashLength17to32(byte[] bytes, int offset, int length) {
        long mul = -7286425919675154353L + (long)(length * 2);
        long a2 = LittleEndianByteArray.load64(bytes, offset) * -5435081209227447693L;
        long b2 = LittleEndianByteArray.load64(bytes, offset + 8);
        long c2 = LittleEndianByteArray.load64(bytes, offset + length - 8) * mul;
        long d2 = LittleEndianByteArray.load64(bytes, offset + length - 16) * -7286425919675154353L;
        return FarmHashFingerprint64.hashLength16(Long.rotateRight(a2 + b2, 43) + Long.rotateRight(c2, 30) + d2, a2 + Long.rotateRight(b2 + -7286425919675154353L, 18) + c2, mul);
    }

    private static long hashLength33To64(byte[] bytes, int offset, int length) {
        long mul = -7286425919675154353L + (long)(length * 2);
        long a2 = LittleEndianByteArray.load64(bytes, offset) * -7286425919675154353L;
        long b2 = LittleEndianByteArray.load64(bytes, offset + 8);
        long c2 = LittleEndianByteArray.load64(bytes, offset + length - 8) * mul;
        long d2 = LittleEndianByteArray.load64(bytes, offset + length - 16) * -7286425919675154353L;
        long y2 = Long.rotateRight(a2 + b2, 43) + Long.rotateRight(c2, 30) + d2;
        long z2 = FarmHashFingerprint64.hashLength16(y2, a2 + Long.rotateRight(b2 + -7286425919675154353L, 18) + c2, mul);
        long e2 = LittleEndianByteArray.load64(bytes, offset + 16) * mul;
        long f2 = LittleEndianByteArray.load64(bytes, offset + 24);
        long g2 = (y2 + LittleEndianByteArray.load64(bytes, offset + length - 32)) * mul;
        long h2 = (z2 + LittleEndianByteArray.load64(bytes, offset + length - 24)) * mul;
        return FarmHashFingerprint64.hashLength16(Long.rotateRight(e2 + f2, 43) + Long.rotateRight(g2, 30) + h2, e2 + Long.rotateRight(f2 + a2, 18) + g2, mul);
    }

    private static long hashLength65Plus(byte[] bytes, int offset, int length) {
        int seed = 81;
        long x2 = 81L;
        long y2 = 2480279821605975764L;
        long z2 = FarmHashFingerprint64.shiftMix(y2 * -7286425919675154353L + 113L) * -7286425919675154353L;
        long[] v2 = new long[2];
        long[] w2 = new long[2];
        x2 = x2 * -7286425919675154353L + LittleEndianByteArray.load64(bytes, offset);
        int end = offset + (length - 1) / 64 * 64;
        int last64offset = end + (length - 1 & 0x3F) - 63;
        do {
            x2 = Long.rotateRight(x2 + y2 + v2[0] + LittleEndianByteArray.load64(bytes, offset + 8), 37) * -5435081209227447693L;
            y2 = Long.rotateRight(y2 + v2[1] + LittleEndianByteArray.load64(bytes, offset + 48), 42) * -5435081209227447693L;
            z2 = Long.rotateRight(z2 + w2[0], 33) * -5435081209227447693L;
            FarmHashFingerprint64.weakHashLength32WithSeeds(bytes, offset, v2[1] * -5435081209227447693L, (x2 ^= w2[1]) + w2[0], v2);
            FarmHashFingerprint64.weakHashLength32WithSeeds(bytes, offset + 32, z2 + w2[1], (y2 += v2[0] + LittleEndianByteArray.load64(bytes, offset + 40)) + LittleEndianByteArray.load64(bytes, offset + 16), w2);
            long tmp = x2;
            x2 = z2;
            z2 = tmp;
        } while ((offset += 64) != end);
        long mul = -5435081209227447693L + ((z2 & 0xFFL) << 1);
        offset = last64offset;
        w2[0] = w2[0] + (long)(length - 1 & 0x3F);
        v2[0] = v2[0] + w2[0];
        w2[0] = w2[0] + v2[0];
        x2 = Long.rotateRight(x2 + y2 + v2[0] + LittleEndianByteArray.load64(bytes, offset + 8), 37) * mul;
        y2 = Long.rotateRight(y2 + v2[1] + LittleEndianByteArray.load64(bytes, offset + 48), 42) * mul;
        z2 = Long.rotateRight(z2 + w2[0], 33) * mul;
        FarmHashFingerprint64.weakHashLength32WithSeeds(bytes, offset, v2[1] * mul, (x2 ^= w2[1] * 9L) + w2[0], v2);
        FarmHashFingerprint64.weakHashLength32WithSeeds(bytes, offset + 32, z2 + w2[1], (y2 += v2[0] * 9L + LittleEndianByteArray.load64(bytes, offset + 40)) + LittleEndianByteArray.load64(bytes, offset + 16), w2);
        return FarmHashFingerprint64.hashLength16(FarmHashFingerprint64.hashLength16(v2[0], w2[0], mul) + FarmHashFingerprint64.shiftMix(y2) * -4348849565147123417L + x2, FarmHashFingerprint64.hashLength16(v2[1], w2[1], mul) + z2, mul);
    }
}

