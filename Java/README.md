# Gilded Rose starting position in Java

## Run the TextTest Fixture from Command-Line

```
./gradlew -q text
```

### Specify Number of Days

For e.g. 10 days:

```
./gradlew -q text --args 10
```

You should make sure the gradle commands shown above work when you execute them in a terminal before trying to use TextTest (see below).


## Run the TextTest approval test that comes with this project

There are instructions in the [TextTest Readme](../texttests/README.md) for setting up TextTest. What's unusual for the Java version is there are two executables listed in [config.gr](../texttests/config.gr) for Java. The first uses Gradle wrapped in a python script. Uncomment these lines to use it:

    executable:${TEXTTEST_HOME}/Java/texttest_rig.py
    interpreter:python

The other relies on your CLASSPATH being set correctly in [environment.gr](../texttests/environment.gr). Uncomment these lines to use it instead:

    executable:com.gildedrose.TexttestFixture
    interpreter:java

# Gilded Rose starting position in Java

## Run the TextTest Fixture from Command-Line

```
./gradlew -q text
```

### Specify Number of Days

For e.g. 10 days:

```
./gradlew -q text --args 10
```

You should make sure the gradle commands shown above work when you execute them in a terminal before trying to use TextTest (see below).

## Run the TextTest approval test that comes with this project

There are instructions in the [TextTest Readme](../texttests/README.md) for setting up TextTest. What's unusual for the Java version is there are two executables listed in [config.gr](../texttests/config.gr) for Java. The first uses Gradle wrapped in a python script. Uncomment these lines to use it:

    executable:${TEXTTEST_HOME}/Java/texttest_rig.py
    interpreter:python

The other relies on your CLASSPATH being set correctly in [environment.gr](../texttests/environment.gr). Uncomment these lines to use it instead:

    executable:com.gildedrose.TexttestFixture
    interpreter:java

---

## Java 21 Toolchain and Modernization

This project now builds with the Java 21 toolchain via Gradle. You need JDK 21+ installed or let Gradle download a matching toolchain.

- Build and test: `./gradlew build`
- We keep code compatible by default; new features are opt-in.

### Modern Java features used
- Records for small, immutable helper types (ItemSnapshot, UpdateContext).
- Switch expression in ItemClassifier for clarity.
- Optional virtual-thread based parallel execution (feature flag).

## Parallel Updates and Virtual Threads (opt‑in)
By default, updates are single-threaded and observer logging is disabled. You can enable parallel processing and (optionally) virtual threads.

Example:

```java
Item[] items = { new Item("foo", 5, 7) };
ItemUpdaterFactory factory = new ItemUpdaterFactory();
UpdateObserver observer = UpdateObserver.noop(); // or your implementation
boolean parallel = true;
boolean observerEnabled = false;
String correlationId = null;
boolean virtualThreads = true; // requires Java 21+

GildedRose app = new GildedRose(items, factory, parallel, observer, observerEnabled, correlationId, virtualThreads);
app.updateQuality();
```

Notes:
- Parallel mode updates each index independently. Do not pass the same Item reference more than once in the array when using parallel modes.
- With `virtualThreads=true`, the app uses `Executors.newVirtualThreadPerTaskExecutor()`; otherwise it uses parallel streams.

## Observer (Tracing) — optional
You can supply an UpdateObserver implementation and enable it to receive before/after snapshots for each update along with metadata (timestamps, thread id, correlation id).

```java
class LoggingObserver implements UpdateObserver {
  @Override public void onItemUpdated(ItemSnapshot before, ItemSnapshot after, String updaterType, UpdateContext ctx) {
    System.out.printf("[%s] %s: %s -> %s on thread %d%n",
        ctx.correlationId(), updaterType, before, after, ctx.threadId());
  }
}

GildedRose app = new GildedRose(items, factory, false, new LoggingObserver(), true, "batch-123", false);
```

Observer is disabled by default; use the constructor overloads to enable.

## External Extension (SPI)
You can contribute custom item rules without modifying core code by providing an ItemRuleProvider on the classpath (via ServiceLoader).

1) Implement the SPI:

```java
public class MyProvider implements ItemRuleProvider {
  @Override public boolean supports(String name) { return name.startsWith("My Special"); }
  @Override public ItemUpdater updater() { return item -> { /* mutate item in place */ }; }
  @Override public int precedence() { return 5; } // higher wins; built-ins are 0
}
```

2) Register in META-INF/services:
- Create file `META-INF/services/com.gildedrose.ItemRuleProvider` containing the fully qualified class name:
```
com.example.MyProvider
```

3) Precedence and safety:
- Highest `precedence()` wins; ties prefer built-in rules.
- External updaters are wrapped in a guard that preserves item.name, clamps quality to [0..50], and enforces Sulfuras immutability.

---

## Architecture Overview
- GildedRose: orchestrates daily updates over an Item[] array.
- ItemUpdaterFactory: selects an ItemUpdater for a given item via ItemClassifier and external SPI providers.
- ItemClassifier: maps item.name to an ItemType using canonical name constants.
- ItemUpdater implementations: Default, AgedBrie, Backstage, Sulfuras, Conjured; they mutate Item in-place using BaseUpdater helpers which clamp quality and decrement sellIn.
- Quality: centralizes bounds and clamping to [0..50].
- UpdateObserver (optional): receives before/after ItemSnapshot and UpdateContext if enabled.

Note on mutability and invariants:
- Item is intentionally mutable with public fields (per kata); we do not change it.
- Invariants are enforced by updater logic and BaseUpdater helpers: quality is always clamped to [0..50]; sellIn decreases by 1 for non-legendary items; Sulfuras never changes.
- For external extensions via SPI, updaters are wrapped in a GuardedUpdater that preserves item.name, enforces Sulfuras immutability, and clamps quality after the provider runs.
