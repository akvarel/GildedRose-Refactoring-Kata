package com.gildedrose;

/**
 * Strategy interface that applies the Gilded Rose daily update rules to a single {@link Item}.
 * <p>
 * Implementations must:
 * <ul>
 *   <li>Adjust {@code sellIn} and {@code quality} according to the specific item type rules.</li>
 *   <li>Keep {@code quality} within the allowed bounds (0..50) unless otherwise specified by kata rules
 *       (e.g., Sulfuras is a legendary item whose quality never changes and is typically 80).</li>
 *   <li>Not mutate the {@code name}.</li>
 * </ul>
 */
public interface ItemUpdater {
    /**
     * Applies one day of evolution to the given {@link Item} according to its type-specific rules.
     *
     * @param item the item to update; must not be null
     */
    void update(Item item);
}
