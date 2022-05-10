package net.minecraft.util;

import java.util.List;
import java.util.Random;

public class WeightedRandom {
    public static int getTotalWeight(List<? extends Item> collection) {
        int i2 = 0;
        int k2 = collection.size();
        for (int j2 = 0; j2 < k2; ++j2) {
            Item weightedrandom$item = collection.get(j2);
            i2 += weightedrandom$item.itemWeight;
        }
        return i2;
    }

    public static <T extends Item> T getRandomItem(Random random, List<T> collection, int totalWeight) {
        if (totalWeight <= 0) {
            throw new IllegalArgumentException();
        }
        int i2 = random.nextInt(totalWeight);
        return WeightedRandom.getRandomItem(collection, i2);
    }

    public static <T extends Item> T getRandomItem(List<T> collection, int weight) {
        int j2 = collection.size();
        for (int i2 = 0; i2 < j2; ++i2) {
            Item t2 = (Item)collection.get(i2);
            if ((weight -= t2.itemWeight) >= 0) continue;
            return (T)t2;
        }
        return null;
    }

    public static <T extends Item> T getRandomItem(Random random, List<T> collection) {
        return WeightedRandom.getRandomItem(random, collection, WeightedRandom.getTotalWeight(collection));
    }

    public static class Item {
        protected int itemWeight;

        public Item(int itemWeightIn) {
            this.itemWeight = itemWeightIn;
        }
    }
}

