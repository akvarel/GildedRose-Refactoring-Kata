package com.gildedrose;

import java.util.Objects;

/**
 * SPI for providing custom ItemUpdater implementations.
 * Implementations are discovered via ServiceLoader on the classpath.
 * Higher precedence wins. Built-in rules are treated as precedence 0 and win ties.
 */
public interface ItemRuleProvider {
    /**
     * @param itemName the Item.name
     * @return true if this provider supplies an updater for this item
     */
    boolean supports(String itemName);

    /**
     * @return the updater to apply when supports(name) is true. Implementations should be stateless.
     */
    ItemUpdater updater();

    /**
     * Precedence for conflict resolution. Higher number wins.
     * Built-in rules are considered precedence 0; to override built-ins, return > 0.
     */
    int precedence();

    /** Utility to compare providers deterministically. */
    static int compare(ItemRuleProvider a, ItemRuleProvider b) {
        int byPrec = Integer.compare(a.precedence(), b.precedence());
        if (byPrec != 0) return byPrec;
        // deterministic tie-breaker by class name (ascending)
        return a.getClass().getName().compareTo(b.getClass().getName());
    }

    static void requireStateless(ItemUpdater updater) {
        Objects.requireNonNull(updater, "updater");
    }
}
