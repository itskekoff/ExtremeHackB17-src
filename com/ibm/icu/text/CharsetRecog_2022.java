package com.ibm.icu.text;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import com.ibm.icu.text.CharsetRecognizer;

abstract class CharsetRecog_2022
extends CharsetRecognizer {
    CharsetRecog_2022() {
    }

    int match(byte[] text, int textLen, byte[][] escapeSequences) {
        int hits = 0;
        int misses = 0;
        int shifts = 0;
        block0: for (int i2 = 0; i2 < textLen; ++i2) {
            if (text[i2] == 27) {
                block1: for (int escN = 0; escN < escapeSequences.length; ++escN) {
                    byte[] seq = escapeSequences[escN];
                    if (textLen - i2 < seq.length) continue;
                    for (int j2 = 1; j2 < seq.length; ++j2) {
                        if (seq[j2] != text[i2 + j2]) continue block1;
                    }
                    ++hits;
                    i2 += seq.length - 1;
                    continue block0;
                }
                ++misses;
            }
            if (text[i2] != 14 && text[i2] != 15) continue;
            ++shifts;
        }
        if (hits == 0) {
            return 0;
        }
        int quality = (100 * hits - 100 * misses) / (hits + misses);
        if (hits + shifts < 5) {
            quality -= (5 - (hits + shifts)) * 10;
        }
        if (quality < 0) {
            quality = 0;
        }
        return quality;
    }

    static class CharsetRecog_2022CN
    extends CharsetRecog_2022 {
        private byte[][] escapeSequences = new byte[][]{{27, 36, 41, 65}, {27, 36, 41, 71}, {27, 36, 42, 72}, {27, 36, 41, 69}, {27, 36, 43, 73}, {27, 36, 43, 74}, {27, 36, 43, 75}, {27, 36, 43, 76}, {27, 36, 43, 77}, {27, 78}, {27, 79}};

        CharsetRecog_2022CN() {
        }

        String getName() {
            return "ISO-2022-CN";
        }

        CharsetMatch match(CharsetDetector det) {
            int confidence = this.match(det.fInputBytes, det.fInputLen, this.escapeSequences);
            return confidence == 0 ? null : new CharsetMatch(det, this, confidence);
        }
    }

    static class CharsetRecog_2022KR
    extends CharsetRecog_2022 {
        private byte[][] escapeSequences = new byte[][]{{27, 36, 41, 67}};

        CharsetRecog_2022KR() {
        }

        String getName() {
            return "ISO-2022-KR";
        }

        CharsetMatch match(CharsetDetector det) {
            int confidence = this.match(det.fInputBytes, det.fInputLen, this.escapeSequences);
            return confidence == 0 ? null : new CharsetMatch(det, this, confidence);
        }
    }

    static class CharsetRecog_2022JP
    extends CharsetRecog_2022 {
        private byte[][] escapeSequences = new byte[][]{{27, 36, 40, 67}, {27, 36, 40, 68}, {27, 36, 64}, {27, 36, 65}, {27, 36, 66}, {27, 38, 64}, {27, 40, 66}, {27, 40, 72}, {27, 40, 73}, {27, 40, 74}, {27, 46, 65}, {27, 46, 70}};

        CharsetRecog_2022JP() {
        }

        String getName() {
            return "ISO-2022-JP";
        }

        CharsetMatch match(CharsetDetector det) {
            int confidence = this.match(det.fInputBytes, det.fInputLen, this.escapeSequences);
            return confidence == 0 ? null : new CharsetMatch(det, this, confidence);
        }
    }
}

