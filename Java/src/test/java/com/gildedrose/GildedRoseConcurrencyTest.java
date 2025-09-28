package com.gildedrose;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GildedRoseConcurrencyTest {

    private static Item copy(Item it) {
        return new Item(it.name, it.sellIn, it.quality);
    }

    @Test
    void parallel_and_serial_produce_identical_results_without_aliasing() {
        Random rnd = new Random(42);
        int n = 5000;
        Item[] original = new Item[n];
        String[] names = new String[]{
                "+5 Dexterity Vest",
                ItemClassifier.NAME_AGED_BRIE,
                "Elixir of the Mongoose",
                ItemClassifier.NAME_SULFURAS,
                ItemClassifier.NAME_BACKSTAGE,
                ItemClassifier.NAME_CONJURED,
                "foo", "bar", "baz"
        };
        for (int i = 0; i < n; i++) {
            String name = names[rnd.nextInt(names.length)];
            int sellIn = rnd.nextInt(40) - 10; // [-10,29]
            int quality = rnd.nextInt(51);     // [0,50]
            original[i] = new Item(name, sellIn, quality);
        }
        // deep copies for two independent runs
        Item[] serialItems = new Item[n];
        Item[] parallelItems = new Item[n];
        for (int i = 0; i < n; i++) {
            serialItems[i] = copy(original[i]);
            parallelItems[i] = copy(original[i]);
        }

        GildedRose serial = new GildedRose(serialItems, new ItemUpdaterFactory());
        GildedRose parallel = new GildedRose(parallelItems, new ItemUpdaterFactory(), true, UpdateObserver.noop(), false);

        for (int day = 0; day < 15; day++) {
            serial.updateQuality();
            parallel.updateQuality();
        }

        for (int i = 0; i < n; i++) {
            assertEquals(serialItems[i].sellIn, parallelItems[i].sellIn, "sellIn mismatch at index " + i + ": " + parallelItems[i]);
            assertEquals(serialItems[i].quality, parallelItems[i].quality, "quality mismatch at index " + i + ": " + parallelItems[i]);
        }
    }
}
