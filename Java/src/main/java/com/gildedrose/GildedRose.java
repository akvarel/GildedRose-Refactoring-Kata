package com.gildedrose;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

class GildedRose implements GildedRoseInterface {
    private final ItemUpdaterFactory factory;
    Item[] items;

    // observability & concurrency flags (default off)
    private final boolean observerEnabled;
    private final UpdateObserver observer;
    private final boolean parallel;
    private final boolean virtualThreads;
    private final String correlationId;

    public GildedRose(Item[] items, ItemUpdaterFactory factory) {
        this(items, factory, false, UpdateObserver.noop(), false, null, false);
    }

    public GildedRose(Item[] items, ItemUpdaterFactory factory, boolean parallel, UpdateObserver observer, boolean observerEnabled) {
        this(items, factory, parallel, observer == null ? UpdateObserver.noop() : observer, observerEnabled, null, false);
    }

    public GildedRose(Item[] items, ItemUpdaterFactory factory, boolean parallel, UpdateObserver observer, boolean observerEnabled, String correlationId) {
        this(items, factory, parallel, observer, observerEnabled, correlationId, false);
    }

    public GildedRose(Item[] items, ItemUpdaterFactory factory, boolean parallel, UpdateObserver observer, boolean observerEnabled, String correlationId, boolean virtualThreads) {
        this.items = items;
        this.factory = factory;
        this.parallel = parallel;
        this.observer = observer == null ? UpdateObserver.noop() : observer;
        this.observerEnabled = observerEnabled;
        this.correlationId = correlationId;
        this.virtualThreads = virtualThreads;
    }

    public void updateQuality() {
        if (parallel) {
            if (virtualThreads) {
                runParallelWithVirtualThreads();
            } else {
                IntStream.range(0, items.length).parallel().forEach(this::updateOne);
            }
        } else {
            for (int i = 0; i < items.length; i++) updateOne(i);
        }
    }

    private void runParallelWithVirtualThreads() {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            try {
                List<Future<?>> futures = new ArrayList<>(items.length);
                for (int i = 0; i < items.length; i++) {
                    final int idx = i;
                    futures.add(executor.submit(() -> updateOne(idx)));
                }
                for (Future<?> f : futures) {
                    try {
                        f.get();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e.getCause());
                    }
                }
            } finally {
                executor.shutdown();
            }
        }
    }

    private void updateOne(int index) {
        Item item = items[index];
        ItemUpdater updater = factory.forItem(item);
        ItemSnapshot before = null;
        if (observerEnabled)
            before = ItemSnapshot.of(item);

        updater.update(item);
        if (observerEnabled) {
            ItemSnapshot after = ItemSnapshot.of(item);
            UpdateContext ctx = UpdateContext.create(correlationId);
            String updaterType = updater.getClass().getSimpleName();
            observer.onItemUpdated(before, after, updaterType, ctx);
        }
    }
}
