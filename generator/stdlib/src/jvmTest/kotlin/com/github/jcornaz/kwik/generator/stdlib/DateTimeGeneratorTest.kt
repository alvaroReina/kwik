package com.github.jcornaz.kwik.generator.stdlib

import com.github.jcornaz.kwik.generator.api.Generator
import com.github.jcornaz.kwik.generator.api.randomSequence
import com.github.jcornaz.kwik.generator.test.AbstractGeneratorTest
import java.time.Duration
import java.time.Instant
import java.time.LocalTime
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class DurationGeneratorTest : AbstractGeneratorTest() {
    override val generator: Generator<Duration> = Generator.durations(Duration.ZERO, Duration.ofDays(100))

    @Test
    fun failForInvalidRange() {
        assertFailsWith<IllegalArgumentException> {
            Generator.durations(Duration.ofSeconds(1), Duration.ZERO)
        }
    }
}

class InstantGeneratorTest : AbstractGeneratorTest() {
    override val generator: Generator<Instant> = Generator.instants(Instant.EPOCH, Instant.now())

    @Test
    fun failForInvalidRange() {
        assertFailsWith<IllegalArgumentException> {
            Generator.instants(Instant.now(), Instant.EPOCH)
        }
    }

    @Test
    fun produceInsideGivenRange() {
        val min = Instant.now()
        val max = min.plusSeconds(50)
        assertTrue(Generator.instants(min = min, max = max).randomSequence(0).take(50).all { it >= min && it <= max })
    }

    @Test
    fun doNotProduceEpochIfNotInRange() {
        val min = Instant.now()
        val max = min.plusSeconds(50)
        assertTrue(Generator.instants(min = min, max = max).randomSequence(0).take(50).none { it == Instant.EPOCH })
    }

    @Test
    fun doNotProduceFarPastIfNotInRange() {
        val min = Instant.now()
        val max = min.plusSeconds(50)
        assertTrue(Generator.instants(min = min, max = max).randomSequence(0).take(50).none { it == Instant.MIN })
    }

    @Test
    fun doNotProduceFarFutureIfNotInRange() {
        val min = Instant.now()
        val max = min.plusSeconds(50)
        assertTrue(Generator.instants(min = min, max = max).randomSequence(0).take(50).none { it == Instant.MAX })
    }

    @Test
    fun generateEpoch() {
        assertTrue(Generator.instants().randomSequence(0).take(50).any { it == Instant.EPOCH })
    }

    @Test
    fun generateFarPast() {

        assertTrue(Generator.instants().randomSequence(0).take(50).any { it == Instant.MIN })
    }

    @Test
    fun generateFarFuture() {
        assertTrue(Generator.instants().randomSequence(0).take(50).any { it == Instant.MAX })
    }

    @Test
    fun generateMax() {
        val min = Instant.now()
        val max = min.plusSeconds(50)
        assertTrue(Generator.instants(min = min, max = max).randomSequence(0).take(50).any { it == max })
    }

    @Test
    fun generateMin() {
        val min = Instant.now()
        val max = min.plusSeconds(50)
        assertTrue(Generator.instants(min = min, max = max).randomSequence(0).take(50).any { it == min })
    }
}

class LocalTimeGeneratorTest : AbstractGeneratorTest() {
    override val generator: Generator<LocalTime> = Generator.localTimes()

    @Test
    fun failForInvalidMax() {
        assertFailsWith<IllegalArgumentException> {
            Generator.localTimes(LocalTime.MAX, LocalTime.MIN)
        }
    }
}