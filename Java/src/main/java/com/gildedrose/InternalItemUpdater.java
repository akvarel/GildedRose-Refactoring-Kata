package com.gildedrose;

// Package-private sealed internal updater type to constrain built-in hierarchy
// while keeping public ItemUpdater open for SPI providers. Internal code uses
// this type; external providers are wrapped into a GuardedUpdater adapter.
sealed interface InternalItemUpdater extends ItemUpdater permits BaseUpdater, ItemUpdaterFactory.GuardedUpdater {
}
