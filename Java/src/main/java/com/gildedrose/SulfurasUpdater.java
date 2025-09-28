package com.gildedrose;

/**
 * Sulfuras (legendary) rule:
 * - No changes to sellIn or quality. Item remains constant across updates.
 */
final class SulfurasUpdater extends BaseUpdater {
    @Override
    public void update(Item item) {
        // Legendary: no changes
    }
}
