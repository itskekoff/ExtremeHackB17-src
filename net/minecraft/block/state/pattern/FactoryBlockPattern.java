package net.minecraft.block.state.pattern;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.pattern.BlockPattern;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class FactoryBlockPattern {
    private static final Joiner COMMA_JOIN = Joiner.on(",");
    private final List<String[]> depth = Lists.newArrayList();
    private final Map<Character, Predicate<BlockWorldState>> symbolMap = Maps.newHashMap();
    private int aisleHeight;
    private int rowWidth;

    private FactoryBlockPattern() {
        this.symbolMap.put(Character.valueOf(' '), Predicates.alwaysTrue());
    }

    public FactoryBlockPattern aisle(String ... aisle) {
        if (!ArrayUtils.isEmpty(aisle) && !StringUtils.isEmpty(aisle[0])) {
            if (this.depth.isEmpty()) {
                this.aisleHeight = aisle.length;
                this.rowWidth = aisle[0].length();
            }
            if (aisle.length != this.aisleHeight) {
                throw new IllegalArgumentException("Expected aisle with height of " + this.aisleHeight + ", but was given one with a height of " + aisle.length + ")");
            }
            String[] arrstring = aisle;
            int n2 = aisle.length;
            for (int i2 = 0; i2 < n2; ++i2) {
                String s2 = arrstring[i2];
                if (s2.length() != this.rowWidth) {
                    throw new IllegalArgumentException("Not all rows in the given aisle are the correct width (expected " + this.rowWidth + ", found one with " + s2.length() + ")");
                }
                for (char c0 : s2.toCharArray()) {
                    if (this.symbolMap.containsKey(Character.valueOf(c0))) continue;
                    this.symbolMap.put(Character.valueOf(c0), null);
                }
            }
            this.depth.add(aisle);
            return this;
        }
        throw new IllegalArgumentException("Empty pattern for aisle");
    }

    public static FactoryBlockPattern start() {
        return new FactoryBlockPattern();
    }

    public FactoryBlockPattern where(char symbol, Predicate<BlockWorldState> blockMatcher) {
        this.symbolMap.put(Character.valueOf(symbol), blockMatcher);
        return this;
    }

    public BlockPattern build() {
        return new BlockPattern(this.makePredicateArray());
    }

    private Predicate<BlockWorldState>[][][] makePredicateArray() {
        this.checkMissingPredicates();
        Predicate[][][] predicate = (Predicate[][][])Array.newInstance(Predicate.class, this.depth.size(), this.aisleHeight, this.rowWidth);
        for (int i2 = 0; i2 < this.depth.size(); ++i2) {
            for (int j2 = 0; j2 < this.aisleHeight; ++j2) {
                for (int k2 = 0; k2 < this.rowWidth; ++k2) {
                    predicate[i2][j2][k2] = this.symbolMap.get(Character.valueOf(this.depth.get(i2)[j2].charAt(k2)));
                }
            }
        }
        return predicate;
    }

    private void checkMissingPredicates() {
        ArrayList<Character> list = Lists.newArrayList();
        for (Map.Entry<Character, Predicate<BlockWorldState>> entry : this.symbolMap.entrySet()) {
            if (entry.getValue() != null) continue;
            list.add(entry.getKey());
        }
        if (!list.isEmpty()) {
            throw new IllegalStateException("Predicates for character(s) " + COMMA_JOIN.join(list) + " are missing");
        }
    }
}

