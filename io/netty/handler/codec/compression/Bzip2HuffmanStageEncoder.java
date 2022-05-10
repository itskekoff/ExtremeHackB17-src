package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.compression.Bzip2BitWriter;
import io.netty.handler.codec.compression.Bzip2HuffmanAllocator;
import io.netty.handler.codec.compression.Bzip2MoveToFrontTable;
import java.util.Arrays;

final class Bzip2HuffmanStageEncoder {
    private static final int HUFFMAN_HIGH_SYMBOL_COST = 15;
    private final Bzip2BitWriter writer;
    private final char[] mtfBlock;
    private final int mtfLength;
    private final int mtfAlphabetSize;
    private final int[] mtfSymbolFrequencies;
    private final int[][] huffmanCodeLengths;
    private final int[][] huffmanMergedCodeSymbols;
    private final byte[] selectors;

    Bzip2HuffmanStageEncoder(Bzip2BitWriter writer, char[] mtfBlock, int mtfLength, int mtfAlphabetSize, int[] mtfSymbolFrequencies) {
        this.writer = writer;
        this.mtfBlock = mtfBlock;
        this.mtfLength = mtfLength;
        this.mtfAlphabetSize = mtfAlphabetSize;
        this.mtfSymbolFrequencies = mtfSymbolFrequencies;
        int totalTables = Bzip2HuffmanStageEncoder.selectTableCount(mtfLength);
        this.huffmanCodeLengths = new int[totalTables][mtfAlphabetSize];
        this.huffmanMergedCodeSymbols = new int[totalTables][mtfAlphabetSize];
        this.selectors = new byte[(mtfLength + 50 - 1) / 50];
    }

    private static int selectTableCount(int mtfLength) {
        if (mtfLength >= 2400) {
            return 6;
        }
        if (mtfLength >= 1200) {
            return 5;
        }
        if (mtfLength >= 600) {
            return 4;
        }
        if (mtfLength >= 200) {
            return 3;
        }
        return 2;
    }

    private static void generateHuffmanCodeLengths(int alphabetSize, int[] symbolFrequencies, int[] codeLengths) {
        int i2;
        int[] mergedFrequenciesAndIndices = new int[alphabetSize];
        int[] sortedFrequencies = new int[alphabetSize];
        for (i2 = 0; i2 < alphabetSize; ++i2) {
            mergedFrequenciesAndIndices[i2] = symbolFrequencies[i2] << 9 | i2;
        }
        Arrays.sort(mergedFrequenciesAndIndices);
        for (i2 = 0; i2 < alphabetSize; ++i2) {
            sortedFrequencies[i2] = mergedFrequenciesAndIndices[i2] >>> 9;
        }
        Bzip2HuffmanAllocator.allocateHuffmanCodeLengths(sortedFrequencies, 20);
        for (i2 = 0; i2 < alphabetSize; ++i2) {
            codeLengths[mergedFrequenciesAndIndices[i2] & 511] = sortedFrequencies[i2];
        }
    }

    private void generateHuffmanOptimisationSeeds() {
        int[][] huffmanCodeLengths = this.huffmanCodeLengths;
        int[] mtfSymbolFrequencies = this.mtfSymbolFrequencies;
        int mtfAlphabetSize = this.mtfAlphabetSize;
        int totalTables = huffmanCodeLengths.length;
        int remainingLength = this.mtfLength;
        int lowCostEnd = -1;
        for (int i2 = 0; i2 < totalTables; ++i2) {
            int actualCumulativeFrequency;
            int targetCumulativeFrequency = remainingLength / (totalTables - i2);
            int lowCostStart = lowCostEnd + 1;
            for (actualCumulativeFrequency = 0; actualCumulativeFrequency < targetCumulativeFrequency && lowCostEnd < mtfAlphabetSize - 1; actualCumulativeFrequency += mtfSymbolFrequencies[++lowCostEnd]) {
            }
            if (lowCostEnd > lowCostStart && i2 != 0 && i2 != totalTables - 1 && (totalTables - i2 & 1) == 0) {
                actualCumulativeFrequency -= mtfSymbolFrequencies[lowCostEnd--];
            }
            int[] tableCodeLengths = huffmanCodeLengths[i2];
            for (int j2 = 0; j2 < mtfAlphabetSize; ++j2) {
                if (j2 >= lowCostStart && j2 <= lowCostEnd) continue;
                tableCodeLengths[j2] = 15;
            }
            remainingLength -= actualCumulativeFrequency;
        }
    }

    private void optimiseSelectorsAndHuffmanTables(boolean storeSelectors) {
        char[] mtfBlock = this.mtfBlock;
        byte[] selectors = this.selectors;
        int[][] huffmanCodeLengths = this.huffmanCodeLengths;
        int mtfLength = this.mtfLength;
        int mtfAlphabetSize = this.mtfAlphabetSize;
        int totalTables = huffmanCodeLengths.length;
        int[][] tableFrequencies = new int[totalTables][mtfAlphabetSize];
        int selectorIndex = 0;
        int groupStart = 0;
        while (groupStart < mtfLength) {
            int groupEnd = Math.min(groupStart + 50, mtfLength) - 1;
            short[] cost = new short[totalTables];
            for (int i2 = groupStart; i2 <= groupEnd; ++i2) {
                char value = mtfBlock[i2];
                for (int j2 = 0; j2 < totalTables; ++j2) {
                    int n2 = j2;
                    cost[n2] = (short)(cost[n2] + huffmanCodeLengths[j2][value]);
                }
            }
            int bestTable = 0;
            short bestCost = cost[0];
            for (int i3 = 1; i3 < totalTables; i3 = (int)((byte)(i3 + 1))) {
                short tableCost = cost[i3];
                if (tableCost >= bestCost) continue;
                bestCost = tableCost;
                bestTable = i3;
            }
            int[] bestGroupFrequencies = tableFrequencies[bestTable];
            for (int i4 = groupStart; i4 <= groupEnd; ++i4) {
                char c2 = mtfBlock[i4];
                bestGroupFrequencies[c2] = bestGroupFrequencies[c2] + 1;
            }
            if (storeSelectors) {
                selectors[selectorIndex++] = bestTable;
            }
            groupStart = groupEnd + 1;
        }
        for (int i5 = 0; i5 < totalTables; ++i5) {
            Bzip2HuffmanStageEncoder.generateHuffmanCodeLengths(mtfAlphabetSize, tableFrequencies[i5], huffmanCodeLengths[i5]);
        }
    }

    private void assignHuffmanCodeSymbols() {
        int[][] huffmanMergedCodeSymbols = this.huffmanMergedCodeSymbols;
        int[][] huffmanCodeLengths = this.huffmanCodeLengths;
        int mtfAlphabetSize = this.mtfAlphabetSize;
        int totalTables = huffmanCodeLengths.length;
        for (int i2 = 0; i2 < totalTables; ++i2) {
            int[] tableLengths = huffmanCodeLengths[i2];
            int minimumLength = 32;
            int maximumLength = 0;
            for (int j2 = 0; j2 < mtfAlphabetSize; ++j2) {
                int length = tableLengths[j2];
                if (length > maximumLength) {
                    maximumLength = length;
                }
                if (length >= minimumLength) continue;
                minimumLength = length;
            }
            int code = 0;
            for (int j3 = minimumLength; j3 <= maximumLength; ++j3) {
                for (int k2 = 0; k2 < mtfAlphabetSize; ++k2) {
                    if ((huffmanCodeLengths[i2][k2] & 0xFF) != j3) continue;
                    huffmanMergedCodeSymbols[i2][k2] = j3 << 24 | code;
                    ++code;
                }
                code <<= 1;
            }
        }
    }

    private void writeSelectorsAndHuffmanTables(ByteBuf out) {
        Bzip2BitWriter writer = this.writer;
        byte[] selectors = this.selectors;
        int totalSelectors = selectors.length;
        int[][] huffmanCodeLengths = this.huffmanCodeLengths;
        int totalTables = huffmanCodeLengths.length;
        int mtfAlphabetSize = this.mtfAlphabetSize;
        writer.writeBits(out, 3, totalTables);
        writer.writeBits(out, 15, totalSelectors);
        Bzip2MoveToFrontTable selectorMTF = new Bzip2MoveToFrontTable();
        for (byte selector : selectors) {
            writer.writeUnary(out, selectorMTF.valueToFront(selector));
        }
        for (int[] tableLengths : huffmanCodeLengths) {
            int currentLength = tableLengths[0];
            writer.writeBits(out, 5, currentLength);
            for (int j2 = 0; j2 < mtfAlphabetSize; ++j2) {
                int codeLength = tableLengths[j2];
                int value = currentLength < codeLength ? 2 : 3;
                int delta = Math.abs(codeLength - currentLength);
                while (delta-- > 0) {
                    writer.writeBits(out, 2, value);
                }
                writer.writeBoolean(out, false);
                currentLength = codeLength;
            }
        }
    }

    private void writeBlockData(ByteBuf out) {
        Bzip2BitWriter writer = this.writer;
        int[][] huffmanMergedCodeSymbols = this.huffmanMergedCodeSymbols;
        byte[] selectors = this.selectors;
        char[] mtf = this.mtfBlock;
        int mtfLength = this.mtfLength;
        int selectorIndex = 0;
        int mtfIndex = 0;
        while (mtfIndex < mtfLength) {
            int groupEnd = Math.min(mtfIndex + 50, mtfLength) - 1;
            int[] tableMergedCodeSymbols = huffmanMergedCodeSymbols[selectors[selectorIndex++]];
            while (mtfIndex <= groupEnd) {
                int mergedCodeSymbol = tableMergedCodeSymbols[mtf[mtfIndex++]];
                writer.writeBits(out, mergedCodeSymbol >>> 24, mergedCodeSymbol);
            }
        }
    }

    void encode(ByteBuf out) {
        this.generateHuffmanOptimisationSeeds();
        for (int i2 = 3; i2 >= 0; --i2) {
            this.optimiseSelectorsAndHuffmanTables(i2 == 0);
        }
        this.assignHuffmanCodeSymbols();
        this.writeSelectorsAndHuffmanTables(out);
        this.writeBlockData(out);
    }
}

