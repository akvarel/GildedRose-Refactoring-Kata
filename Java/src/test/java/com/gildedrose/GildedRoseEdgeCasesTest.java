package com.gildedrose;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GildedRoseEdgeCasesTest {

    private static GildedRose app(Item... items) {
        return new GildedRose(items, new ItemUpdaterFactory());
    }

    @Test
    void defaultItem_sellIn_zero_then_degrades_twice_after() {
        Item it = new Item("+5 Dexterity Vest", 0, 3);
        GildedRose gr = app(it);
        // day 1: decrease 1, then sellIn--, and since sellIn<0 afterwards, decrease one more (total -2)
        gr.updateQuality();
        assertEquals(-1, it.sellIn);
        assertEquals(1, it.quality);
        // day 2: after sell date, decrease twice as fast (-2) but clamp at 0
        gr.updateQuality();
        assertEquals(-2, it.sellIn);
        assertEquals(0, it.quality); // clamped at 0
    }

    @Test
    void defaultItem_quality_does_not_go_negative() {
        Item it = new Item("Elixir of the Mongoose", 5, 0);
        GildedRose gr = app(it);
        gr.updateQuality();
        assertEquals(4, it.sellIn);
        assertEquals(0, it.quality);
    }

    @Test
    void agedBrie_caps_at_50_and_doubles_after_sell_date() {
        Item brie = new Item("Aged Brie", 0, 49);
        GildedRose gr = app(brie);
        // day 1: +1 then sellIn--
        gr.updateQuality();
        assertEquals(-1, brie.sellIn);
        assertEquals(50, brie.quality);
        // day 2: would be +2 but still capped at 50
        gr.updateQuality();
        assertEquals(-2, brie.sellIn);
        assertEquals(50, brie.quality);
    }

    @Test
    void backstage_thresholds_11_10_and_6_5_and_drop_to_zero_after_concert() {
        Item d11 = new Item("Backstage passes to a TAFKAL80ETC concert", 11, 10);
        Item d10 = new Item("Backstage passes to a TAFKAL80ETC concert", 10, 10);
        Item d6  = new Item("Backstage passes to a TAFKAL80ETC concert", 6, 10);
        Item d5  = new Item("Backstage passes to a TAFKAL80ETC concert", 5, 10);
        Item d0  = new Item("Backstage passes to a TAFKAL80ETC concert", 0, 10);
        GildedRose gr = app(d11, d10, d6, d5, d0);

        gr.updateQuality();
        assertEquals(11, d11.quality); // +1
        assertEquals(12, d10.quality); // +2
        assertEquals(12, d6.quality);  // +2 (<=10 but >5)
        assertEquals(13, d5.quality);  // +3 (<=5)
        assertEquals(0, d0.quality);   // drop to 0 after concert
    }

    @Test
    void conjured_degrades_twice_and_four_after_sell_date_with_floor_zero() {
        Item conj = new Item("Conjured Mana Cake", 0, 3);
        GildedRose gr = app(conj);
        gr.updateQuality();
        assertEquals(-1, conj.sellIn);
        assertEquals(0, conj.quality); // -2 (to 1) then another -2 after sell date => clamp to 0
        gr.updateQuality();
        assertEquals(-2, conj.sellIn);
        assertEquals(0, conj.quality); // stays at 0
    }

    @Test
    void sulfuras_never_changes_even_around_edges() {
        Item sulf = new Item("Sulfuras, Hand of Ragnaros", 0, 80);
        GildedRose gr = app(sulf);
        for (int i = 0; i < 5; i++) gr.updateQuality();
        assertEquals(0, sulf.sellIn);
        assertEquals(80, sulf.quality);
    }

    @Test
    void quality_never_exceeds_50_even_with_backstage_bonuses() {
        Item backstage = new Item("Backstage passes to a TAFKAL80ETC concert", 5, 49);
        GildedRose gr = app(backstage);
        gr.updateQuality(); // +3 => would be 52 but should clamp to 50
        assertEquals(4, backstage.sellIn);
        assertEquals(50, backstage.quality);
    }
}
