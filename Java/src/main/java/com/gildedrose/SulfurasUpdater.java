package com.gildedrose;

/**
 * Sulfuras (legendary) rule:
 * - No changes to sellIn or quality. Item remains constant across updates.
 */
class SulfurasUpdater implements ItemUpdater {
    @Override
    public void update(Item item) {
        // Legendary: no changes
    }
}
