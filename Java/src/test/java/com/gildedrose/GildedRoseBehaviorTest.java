package com.gildedrose;

import com.gildedrose.old.GildedRoseOld;
import com.gildedrose.simple.GildedRoseSimple;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class GildedRoseBehaviorTest {

    private static GildedRose app(Item... items) {
        return new GildedRose(items, new ItemUpdaterFactory());
    }

    private static void updateDays(GildedRose app, int days) {
        for (int i = 0; i < days; i++) app.updateQuality();
    }

    @Test
    void normalItem_degrades_by_1_then_by_2_after_sell_date_never_below_zero() {
        Item normal = new Item("Elixir of the Mongoose", 1, 3);
        GildedRose gr = app(normal);
        // day 1: before sell date
        gr.updateQuality();
        assertEquals(0, normal.sellIn);
        assertEquals(2, normal.quality);
        // day 2: after sell date, degrades twice as fast
        gr.updateQuality();
        assertEquals(-1, normal.sellIn);
        assertEquals(0, normal.quality); // would go to 0 not negative
    }

    @Test
    void agedBrie_increases_quality_and_doubles_after_sell_date_capped_at_50() {
        Item brie = new Item("Aged Brie", 1, 49);
        GildedRose gr = app(brie);
        // day 1: +1
        gr.updateQuality();
        assertEquals(0, brie.sellIn);
        assertEquals(50, brie.quality); // capped at 50
        // day 2: would be +2 after sell date but still capped at 50
        gr.updateQuality();
        assertEquals(-1, brie.sellIn);
        assertEquals(50, brie.quality);
    }

    @Test
    void backstage_increments_tiers_and_drops_to_zero_after_concert() {
        Item moreThan10 = new Item("Backstage passes to a TAFKAL80ETC concert", 11, 10);
        Item tenDays = new Item("Backstage passes to a TAFKAL80ETC concert", 10, 10);
        Item fiveDays = new Item("Backstage passes to a TAFKAL80ETC concert", 5, 10);
        Item zeroDays = new Item("Backstage passes to a TAFKAL80ETC concert", 0, 10);
        GildedRose gr = app(moreThan10, tenDays, fiveDays, zeroDays);
        gr.updateQuality();
        assertEquals(11, moreThan10.quality); // +1
        assertEquals(12, tenDays.quality);    // +2
        assertEquals(13, fiveDays.quality);   // +3
        assertEquals(0, zeroDays.quality);    // drop to 0
    }

    @Test
    void sulfuras_never_changes_sellIn_or_quality() {
        Random rnd = new Random(123);
        Item sulf = new Item("Sulfuras, Hand of Ragnaros", 0, 80);
        GildedRose gr = app(sulf);
        updateDays(gr, rnd.nextInt(30));
        assertEquals(0, sulf.sellIn);
        assertEquals(80, sulf.quality);
    }

    @Test
    void conjured_degrades_twice_as_fast_and_never_below_zero() {
        Item conj = new Item("Conjured Mana Cake", 1, 3);
        GildedRose gr = app(conj);
        // day 1: -2
        gr.updateQuality();
        assertEquals(0, conj.sellIn);
        assertEquals(1, conj.quality);
        // day 2: after sell date, -4 but capped at 0
        gr.updateQuality();
        assertEquals(-1, conj.sellIn);
        assertEquals(0, conj.quality);
    }

    @Test
    void unknown_item_uses_default_rules() {
        Item unknown = new Item("Some Random Thing", 11, 10);
        GildedRose gr = app(unknown);
        gr.updateQuality();
        assertEquals(10, unknown.sellIn);
        assertEquals(9, unknown.quality);
        gr.updateQuality();
        assertEquals(9, unknown.sellIn);
        assertEquals(8, unknown.quality);
        gr.updateQuality();
        assertEquals(8, unknown.sellIn);
        assertEquals(7, unknown.quality);
        gr.updateQuality();
        assertEquals(7, unknown.sellIn);
        assertEquals(6, unknown.quality);
        gr.updateQuality();
        assertEquals(6, unknown.sellIn);
        assertEquals(5, unknown.quality);
    }

    @Test
    void invariants_hold_across_random_items_over_many_days_with_performance_data() {
        int size = Integer.getInteger("perf.dataset", 20_000);
        int days = Integer.getInteger("perf.days", 20);
        int repeats = Integer.getInteger("perf.repeats", 1);

        // We validate invariants across five variants of the engine and ensure they all run on EXACTLY the same dataset:
        // 1) sequential, 2) parallel stream, 3) virtual-threaded, 4) old code, 5) simple implementation
        String[] modes = new String[]{"sequential", "parallel", "threaded", "old", "simple"};

        for (int r = 0; r < repeats; r++) {
            // Build the base dataset once per repeat to keep copies isolated
            Random rnd = new Random(123);
            Item[] base = new Item[size];
            String[] names = new String[]{
                    "+5 Dexterity Vest",
                    "Aged Brie",
                    "Elixir of the Mongoose",
                    "Sulfuras, Hand of Ragnaros",
                    "Backstage passes to a TAFKAL80ETC concert",
                    "Conjured Mana Cake",
                    "foo", "bar", "baz"
            };
            for (int i = 0; i < base.length; i++) {
                String name = names[rnd.nextInt(names.length)];
                int sellIn = rnd.nextInt(40) - 10; // [-10, 29]
                int quality = rnd.nextInt(51);     // [0, 50]
                base[i] = new Item(name, sellIn, quality);
            }

            for (String mode : modes) {
                // Make a deep copy for this mode so each variant starts from identical data
                Item[] items = new Item[base.length];
                for (int i = 0; i < base.length; i++) {
                    Item b = base[i];
                    items[i] = new Item(b.name, b.sellIn, b.quality);
                }

                // snapshot initial state for Sulfuras to assert immutability precisely
                int[] initialSellIn = new int[items.length];
                int[] initialQuality = new int[items.length];
                for (int i = 0; i < items.length; i++) {
                    initialSellIn[i] = items[i].sellIn;
                    initialQuality[i] = items[i].quality;
                }

                long t1 = System.nanoTime();

                GildedRoseInterface gr;
                switch (mode) {
                    case "parallel":
                        gr = new GildedRose(items, new ItemUpdaterFactory(), true, null, false);
                        break;
                    case "threaded":
                        gr = new GildedRose(items, new ItemUpdaterFactory(), true, null, false, null, true);
                        break;
                    case "old":
                        gr = new GildedRoseOld(items);
                        break;
                    case "simple":
                        gr = new GildedRoseSimple(items, new com.gildedrose.simple.ItemUpdaterFactory());
                        break;
                    default:
                        gr = app(items); // sequential
                }

                for (int d = 0; d < days; d++) {
                    gr.updateQuality();
                    for (int i = 0; i < items.length; i++) {
                        Item it = items[i];
                        // Quality bounds (canonical rules keep within [0,50]; Sulfuras is 80 in kata, but our dataset is 0..50)
                        assertTrue(it.quality >= 0 && it.quality <= 50,
                                "Quality out of bounds for: " + it + " in mode=" + mode);
                        // Sulfuras immutability: sellIn and quality unchanged from initial values
                        if ("Sulfuras, Hand of Ragnaros".equals(it.name)) {
                            assertEquals(initialSellIn[i], it.sellIn, "Sulfuras sellIn changed in mode=" + mode + ": " + it);
                            assertEquals(initialQuality[i], it.quality, "Sulfuras quality changed in mode=" + mode + ": " + it);
                        }
                    }
                }
                long t2 = System.nanoTime();
                System.out.printf("[repeat=%d][%s] Execution time: %.2f ms\n", r, mode, (t2 - t1) / 1e6);
            }
        }
    }
}
