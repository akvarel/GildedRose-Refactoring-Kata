package com.gildedrose;

/**
 * Updater for the legendary item "Sulfuras, Hand of Ragnaros".
 * <p>
 * Rules:
 * <ul>
 *   <li>Neither {@code sellIn} nor {@code quality} ever changes.</li>
 *   <li>In the original kata, Sulfuras quality is constant 80.</li>
 * </ul>
 */
class SulfurasUpdater implements ItemUpdater {
    /**
     * No-op: legendary items do not age or degrade.
     */
    @Override
    public void update(Item item) {
        // Legendary: no changes
    }
}
