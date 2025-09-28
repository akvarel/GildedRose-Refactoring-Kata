package com.gildedrose;

import java.util.*;
import java.util.function.Function;

class ItemUpdaterFactory {
    private final Map<ItemType, ItemUpdater> byType = new HashMap<>();
    private final ItemUpdater defaultUpdater = new DefaultUpdater();
    private final Function<Item, ItemType> classifier;
    private final List<ItemRuleProvider> providers;

    ItemUpdaterFactory() {
        this(ItemClassifier::classify);
    }

    ItemUpdaterFactory(Function<Item, ItemType> classifier) {
        this.classifier = classifier == null ? ItemClassifier::classify : classifier;
        byType.put(ItemType.AGED_BRIE, new AgedBrieUpdater());
        byType.put(ItemType.BACKSTAGE, new BackstageUpdater());
        byType.put(ItemType.SULFURAS, new SulfurasUpdater());
        byType.put(ItemType.CONJURED, new ConjuredUpdater());
        this.providers = loadProviders();
    }

    private List<ItemRuleProvider> loadProviders() {
        List<ItemRuleProvider> list = new ArrayList<>();
        ServiceLoader<ItemRuleProvider> sl = ServiceLoader.load(ItemRuleProvider.class);
        for (ItemRuleProvider p : sl) list.add(p);
        // sort once by precedence asc so that picking the last yields highest precedence; ties deterministic by class name
        list.sort(ItemRuleProvider::compare);
        return Collections.unmodifiableList(list);
    }

    ItemUpdater forItem(Item item) {
        // consult external providers first: pick highest-precedence that supports this name strictly greater than builtins' precedence (0)
        ItemRuleProvider best = null;
        String name = item.name;
        for (ItemRuleProvider p : providers) {
            try {
                if (p.supports(name)) {
                    if (best == null || ItemRuleProvider.compare(p, best) > 0) best = p;
                }
            } catch (Throwable t) {
                // ignore faulty provider
            }
        }
        if (best != null && best.precedence() > 0) {
            ItemUpdater u = best.updater();
            ItemRuleProvider.requireStateless(u);
            return new GuardedUpdater(u);
        }
        ItemType type = classifier.apply(item);
        return byType.getOrDefault(type, defaultUpdater);
    }

    /**
     * Wraps an updater to enforce invariants and safety after external updates.
     */
    static final class GuardedUpdater implements ItemUpdater {
        private final ItemUpdater delegate;
        GuardedUpdater(ItemUpdater delegate) { this.delegate = delegate; }
        @Override public void update(Item item) {
            String originalName = item.name;
            int originalSellIn = item.sellIn;
            int originalQuality = item.quality;
            delegate.update(item);
            // preserve name
            item.name = originalName;
            // enforce Sulfuras invariants strictly
            if (ItemClassifier.NAME_SULFURAS.equals(originalName)) {
                item.sellIn = originalSellIn;
                item.quality = originalQuality;
                return;
            }
            // clamp quality
            item.quality = Quality.clamp(item.quality);
        }
    }
}
