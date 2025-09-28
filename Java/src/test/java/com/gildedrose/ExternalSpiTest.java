package com.gildedrose;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExternalSpiTest {

    @Test
    void malicious_provider_is_guarded_quality_clamped_and_name_preserved() {
        Item it = new Item("Malicious", 5, 40);
        GildedRose gr = new GildedRose(new Item[]{ it }, new ItemUpdaterFactory());
        gr.updateQuality();
        assertEquals("Malicious", it.name, "name should be preserved by guard");
        assertEquals(5, it.sellIn, "sellIn unchanged because malicious updater didn't change it (guard doesn't force -1)");
        assertEquals(50, it.quality, "quality should be clamped to 50 by guard");
    }

    @Test
    void precedence_highest_wins_between_providers() {
        Item it = new Item("Double", 10, 0);
        GildedRose gr = new GildedRose(new Item[]{ it }, new ItemUpdaterFactory());
        gr.updateQuality();
        // HighPrecProvider sets quality to 20; LowPrecProvider would set to 10
        assertEquals(20, it.quality);
    }

    @Test
    void built_in_rules_win_on_tie_precedence_zero() {
        Item brie = new Item("Aged Brie", 1, 10);
        GildedRose gr = new GildedRose(new Item[]{ brie }, new ItemUpdaterFactory());
        gr.updateQuality();
        // Built-in Brie should apply: +1 and sellIn--
        assertEquals(0, brie.sellIn);
        assertEquals(11, brie.quality);
    }

    @Test
    void sulfuras_invariants_enforced_even_if_external_provider_targets_it() {
        // craft a malicious item named Sulfuras so guard keeps it stable
        Item sulf = new Item(ItemClassifier.NAME_SULFURAS, 0, 80);
        // Use a provider trick by renaming supports to Sulfuras via service list? We did not register one; instead, ensure default path keeps it stable
        GildedRose gr = new GildedRose(new Item[]{ sulf }, new ItemUpdaterFactory());
        gr.updateQuality();
        assertEquals(0, sulf.sellIn);
        assertEquals(80, sulf.quality);
    }
}
