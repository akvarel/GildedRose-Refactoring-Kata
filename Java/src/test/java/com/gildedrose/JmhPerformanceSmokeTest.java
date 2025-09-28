package com.gildedrose;

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Runs a tiny JMH benchmark inside a unit test to ensure the JMH suite is wired and executable.
 * This is not meant to be a strict performance gate, only a smoke test.
 */
class JmhPerformanceSmokeTest {

    @Test
    void runJmhBenchmark_smoke() throws Exception {
        Options opt = new OptionsBuilder()
                .include(".*GildedRoseJmhBenchmark.*")
                .warmupIterations(1)
                .measurementIterations(1)
                .forks(1)
                .shouldDoGC(true)
                .build();

        Collection<RunResult> results = new Runner(opt).run();
        assertFalse(results.isEmpty(), "No JMH results produced");
        // Basic sanity: benchmark executed and produced non-negative scores
        for (RunResult r : results) {
            assertTrue(r.getPrimaryResult().getScore() >= 0.0,
                    "Unexpected negative score for " + r.getParams().getBenchmark());
        }
    }
}
