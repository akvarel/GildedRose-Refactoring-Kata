package com.gildedrose;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GildedRoseTest {

    private static GildedRose app(Item... items) {
        return new GildedRose(items);
    }

    private static void updateDays(GildedRose app, int days) {
        for (int i = 0; i < days; i++) app.updateQuality();
    }

    @Test
    void foo() {
        Item[] items = new Item[] { new Item("foo", 0, 0) };
        GildedRose app = new GildedRose(items);
        app.updateQuality();
        assertEquals("foo", app.items[0].name);
    }

    @RepeatedTest(20)
    void invariants_hold_across_random_items_over_many_days() {
        Random rnd = new Random(123);
        Item[] items = new Item[200000];
        String[] names = new String[]{
            "+5 Dexterity Vest",
            "Aged Brie",
            "Elixir of the Mongoose",
            "Sulfuras, Hand of Ragnaros",
            "Backstage passes to a TAFKAL80ETC concert",
            "Conjured Mana Cake",
            "foo", "bar", "baz"
        };
        for (int i = 0; i < items.length; i++) {
            String name = names[rnd.nextInt(names.length)];
            int sellIn = rnd.nextInt(40) - 10; // [-10, 29]
            int quality = rnd.nextInt(51);     // [0, 50]
            items[i] = new Item(name, sellIn, quality);
        }
        // snapshot initial state for Sulfuras to assert immutability precisely
        int[] initialSellIn = new int[items.length];
        int[] initialQuality = new int[items.length];
        for (int i = 0; i < items.length; i++) {
            initialSellIn[i] = items[i].sellIn;
            initialQuality[i] = items[i].quality;
        }
        long t1 = System.nanoTime();

        GildedRose gr = app(items);
        for (int d = 0; d < 35; d++) {
            gr.updateQuality();
            for (int i = 0; i < items.length; i++) {
                Item it = items[i];
                // Quality bounds (canonical rules keep within [0,50]; Sulfuras is 80 in kata, but our dataset is 0..50)
                assertTrue(it.quality >= 0 && it.quality <= 50,
                    "Quality out of bounds for: " + it);
                // Sulfuras immutability: sellIn and quality unchanged from initial values
                if ("Sulfuras, Hand of Ragnaros".equals(it.name)) {
                    assertEquals(initialSellIn[i], it.sellIn, "Sulfuras sellIn changed: " + it);
                    assertEquals(initialQuality[i], it.quality, "Sulfuras quality changed: " + it);
                }
            }
        }
        long t2 = System.nanoTime();
        System.out.printf("Execution time: %.2f ms\n", (t2 - t1)/1e6);
    }

}
