package io.netty.handler.codec.compression;

final class Bzip2HuffmanAllocator {
    private static int first(int[] array, int i2, int nodesToMove) {
        int length = array.length;
        int limit = i2;
        int k2 = array.length - 2;
        while (i2 >= nodesToMove && array[i2] % length > limit) {
            k2 = i2;
            i2 -= limit - i2 + 1;
        }
        i2 = Math.max(nodesToMove - 1, i2);
        while (k2 > i2 + 1) {
            int temp = i2 + k2 >>> 1;
            if (array[temp] % length > limit) {
                k2 = temp;
                continue;
            }
            i2 = temp;
        }
        return k2;
    }

    private static void setExtendedParentPointers(int[] array) {
        int length = array.length;
        array[0] = array[0] + array[1];
        int headNode = 0;
        int topNode = 2;
        for (int tailNode = 1; tailNode < length - 1; ++tailNode) {
            int temp;
            if (topNode >= length || array[headNode] < array[topNode]) {
                temp = array[headNode];
                array[headNode++] = tailNode;
            } else {
                temp = array[topNode++];
            }
            if (topNode >= length || headNode < tailNode && array[headNode] < array[topNode]) {
                temp += array[headNode];
                array[headNode++] = tailNode + length;
            } else {
                temp += array[topNode++];
            }
            array[tailNode] = temp;
        }
    }

    private static int findNodesToRelocate(int[] array, int maximumLength) {
        int currentNode = array.length - 2;
        for (int currentDepth = 1; currentDepth < maximumLength - 1 && currentNode > 1; ++currentDepth) {
            currentNode = Bzip2HuffmanAllocator.first(array, currentNode - 1, 0);
        }
        return currentNode;
    }

    private static void allocateNodeLengths(int[] array) {
        int firstNode = array.length - 2;
        int nextNode = array.length - 1;
        int currentDepth = 1;
        int availableNodes = 2;
        while (availableNodes > 0) {
            int lastNode = firstNode;
            firstNode = Bzip2HuffmanAllocator.first(array, lastNode - 1, 0);
            for (int i2 = availableNodes - (lastNode - firstNode); i2 > 0; --i2) {
                array[nextNode--] = currentDepth;
            }
            availableNodes = lastNode - firstNode << 1;
            ++currentDepth;
        }
    }

    private static void allocateNodeLengthsWithRelocation(int[] array, int nodesToMove, int insertDepth) {
        int firstNode = array.length - 2;
        int nextNode = array.length - 1;
        int currentDepth = insertDepth == 1 ? 2 : 1;
        int nodesLeftToMove = insertDepth == 1 ? nodesToMove - 2 : nodesToMove;
        int availableNodes = currentDepth << 1;
        while (availableNodes > 0) {
            int lastNode = firstNode;
            firstNode = firstNode <= nodesToMove ? firstNode : Bzip2HuffmanAllocator.first(array, lastNode - 1, nodesToMove);
            int offset = 0;
            if (currentDepth >= insertDepth) {
                offset = Math.min(nodesLeftToMove, 1 << currentDepth - insertDepth);
            } else if (currentDepth == insertDepth - 1) {
                offset = 1;
                if (array[firstNode] == lastNode) {
                    ++firstNode;
                }
            }
            for (int i2 = availableNodes - (lastNode - firstNode + offset); i2 > 0; --i2) {
                array[nextNode--] = currentDepth;
            }
            nodesLeftToMove -= offset;
            availableNodes = lastNode - firstNode + offset << 1;
            ++currentDepth;
        }
    }

    static void allocateHuffmanCodeLengths(int[] array, int maximumLength) {
        switch (array.length) {
            case 2: {
                array[1] = 1;
            }
            case 1: {
                array[0] = 1;
                return;
            }
        }
        Bzip2HuffmanAllocator.setExtendedParentPointers(array);
        int nodesToRelocate = Bzip2HuffmanAllocator.findNodesToRelocate(array, maximumLength);
        if (array[0] % array.length >= nodesToRelocate) {
            Bzip2HuffmanAllocator.allocateNodeLengths(array);
        } else {
            int insertDepth = maximumLength - (32 - Integer.numberOfLeadingZeros(nodesToRelocate - 1));
            Bzip2HuffmanAllocator.allocateNodeLengthsWithRelocation(array, nodesToRelocate, insertDepth);
        }
    }

    private Bzip2HuffmanAllocator() {
    }
}

