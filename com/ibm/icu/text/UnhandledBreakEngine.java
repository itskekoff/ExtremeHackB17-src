package com.ibm.icu.text;

import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.text.LanguageBreakEngine;
import com.ibm.icu.text.UnicodeSet;
import java.text.CharacterIterator;
import java.util.Stack;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class UnhandledBreakEngine
implements LanguageBreakEngine {
    private final UnicodeSet[] fHandled = new UnicodeSet[5];

    public UnhandledBreakEngine() {
        for (int i2 = 0; i2 < this.fHandled.length; ++i2) {
            this.fHandled[i2] = new UnicodeSet();
        }
    }

    @Override
    public boolean handles(int c2, int breakType) {
        return breakType >= 0 && breakType < this.fHandled.length && this.fHandled[breakType].contains(c2);
    }

    @Override
    public int findBreaks(CharacterIterator text, int startPos, int endPos, boolean reverse, int breakType, Stack<Integer> foundBreaks) {
        text.setIndex(endPos);
        return 0;
    }

    public synchronized void handleChar(int c2, int breakType) {
        if (breakType >= 0 && breakType < this.fHandled.length && c2 != Integer.MAX_VALUE && !this.fHandled[breakType].contains(c2)) {
            int script = UCharacter.getIntPropertyValue(c2, 4106);
            this.fHandled[breakType].applyIntPropertyValue(4106, script);
        }
    }
}

