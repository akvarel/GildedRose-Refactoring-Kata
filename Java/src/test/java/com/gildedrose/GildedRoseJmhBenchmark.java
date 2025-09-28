package com.gildedrose;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 1)
@Measurement(iterations = 1)
@Fork(1)
@State(Scope.Benchmark)
public class GildedRoseJmhBenchmark {

    @Param({"20000"})
    public int size;

    private Item[] base;
    private String[] names;

    @Setup(Level.Trial)
    public void setup() {
        Random rnd = new Random(123);
        names = new String[]{
                "+5 Dexterity Vest",
                "Aged Brie",
                "Elixir of the Mongoose",
                "Sulfuras, Hand of Ragnaros",
                "Backstage passes to a TAFKAL80ETC concert",
                "Conjured Mana Cake",
                "foo", "bar", "baz"
        };
        base = new Item[size];
        for (int i = 0; i < base.length; i++) {
            String name = names[rnd.nextInt(names.length)];
            int sellIn = rnd.nextInt(40) - 10; // [-10, 29]
            int quality = rnd.nextInt(51);     // [0, 50]
            base[i] = new Item(name, sellIn, quality);
        }
    }

    private Item[] copyBase() {
        Item[] items = new Item[base.length];
        for (int i = 0; i < base.length; i++) {
            Item b = base[i];
            items[i] = new Item(b.name, b.sellIn, b.quality);
        }
        return items;
    }

    private long checksum(Item[] items) {
        long sum = 0;
        for (Item it : items) {
            sum += (long) it.sellIn * 131 + it.quality;
        }
        return sum;
    }

    @Benchmark
    public void sequential(Blackhole bh) {
        Item[] items = copyBase();
        GildedRoseInterface gr = new GildedRose(items, new ItemUpdaterFactory());
        for (int d = 0; d < 20; d++) gr.updateQuality();
        bh.consume(checksum(items));
    }

    @Benchmark
    public void parallelStream(Blackhole bh) {
        Item[] items = copyBase();
        GildedRoseInterface gr = new GildedRose(items, new ItemUpdaterFactory(), true, null, false);
        for (int d = 0; d < 20; d++) gr.updateQuality();
        bh.consume(checksum(items));
    }

    @Benchmark
    public void virtualThreads(Blackhole bh) {
        Item[] items = copyBase();
        GildedRoseInterface gr = new GildedRose(items, new ItemUpdaterFactory(), true, null, false, null, true);
        for (int d = 0; d < 20; d++) gr.updateQuality();
        bh.consume(checksum(items));
    }
}
