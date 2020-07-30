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

private const val MAX_NANOSECONDS = 999_999_999L

class DurationGeneratorTest : AbstractGeneratorTest() {
    override val generator: Generator<Duration> = Generator.durations(Duration.ZERO, Duration.ofDays(100))

    @Test
    fun failForInvalidRange() {
        assertFailsWith<IllegalArgumentException> {
            Generator.durations(Duration.ofSeconds(1), Duration.ZERO)
        }
    }

    @Test
    fun produceInsideGivenRange() {
        val min = Duration.ofMinutes(-1)
        val max = Duration.ofMinutes(1)
        assertTrue(Generator.durations(min = min, max = max).randomSequence(0).take(50).all { it >= min && it <= max })
    }

    @Test
    fun produceInsideGivenNanosecondsRange() {
        val min = Duration.ofSeconds(0, 500)
        val max = Duration.ofSeconds(0, 1000)
        assertTrue(Generator.durations(min = min, max = max).randomSequence(0).take(50).all { it >= min && it <= max })
    }

    @Test
    fun doNotProduceZeroIfNotInRange() {
        val min = Duration.ofMinutes(1)
        assertTrue(Generator.durations(min = min).randomSequence(0).take(50).none { it == Duration.ZERO })
    }

    @Test
    fun doNotProduceGlobalMaxIfNotInRange() {
        val max = Duration.ZERO
        assertTrue(Generator.durations(max = max).randomSequence(0).take(50).none { it == Duration.ofSeconds(Long.MAX_VALUE, MAX_NANOSECONDS) })
    }

    @Test
    fun doNotProduceGlobalMinIfNotInRange() {
        val min = Duration.ZERO
        assertTrue(Generator.durations(min = min).randomSequence(0).take(50).none { it == Duration.ofSeconds(Long.MIN_VALUE) })
    }

    @Test
    fun generateZero() {
        assertTrue(Generator.durations().randomSequence(0).take(50).any { it == Duration.ZERO })
    }

    @Test
    fun generateGlobalMax() {
        assertTrue(Generator.durations().randomSequence(0).take(50).any { it == Duration.ofSeconds(Long.MAX_VALUE, MAX_NANOSECONDS) })
    }

    @Test
    fun generateGlobalMin() {
        assertTrue(Generator.durations().randomSequence(0).take(50).any { it == Duration.ofSeconds(Long.MIN_VALUE) })
    }

    @Test
    fun generateMax() {
        val max = Duration.ofSeconds(1)
        assertTrue(Generator.durations(max = max).randomSequence(0).take(50).any { it == max })
    }

    @Test
    fun generateMin() {
        val min = Duration.ofSeconds(1)
        assertTrue(Generator.durations(min = min).randomSequence(0).take(50).any { it == min })
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
    fun produceInsideGivenNanosecondsRange() {
        val min = Instant.EPOCH
        val max = min.plusNanos(500)
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
    fun failForInvalidRange() {
        assertFailsWith<IllegalArgumentException> {
            Generator.localTimes(LocalTime.MAX, LocalTime.MIN)
        }
    }

    @Test
    fun produceInsideGivenRange() {
        val min = LocalTime.NOON
        val max = min.plusHours(2)
        assertTrue(Generator.localTimes(min = min, max = max).randomSequence(0).take(50).all { it >= min && it <= max })
    }

    @Test
    fun doNotProduceNoonIfNotInRange() {
        val min = LocalTime.NOON.plusHours(2)
        assertTrue(Generator.localTimes(min = min).randomSequence(0).take(50).none { it == LocalTime.NOON })
    }

    @Test
    fun doNotProduceGlobalMaxIfNotInRange() {
        val max = LocalTime.MAX.minusHours(1)
        assertTrue(Generator.localTimes(max = max).randomSequence(0).take(50).none { it == LocalTime.MAX })
    }

    @Test
    fun doNotProduceGlobalMinIfNotInRange() {
        val min = LocalTime.MIN.plusHours(1)
        assertTrue(Generator.localTimes(min = min).randomSequence(0).take(50).none { it == LocalTime.MIN })
    }

    @Test
    fun generateNoon() {
        assertTrue(Generator.localTimes().randomSequence(0).take(50).any { it == LocalTime.NOON })
    }

    @Test
    fun generateGlobalMax() {
        assertTrue(Generator.localTimes().randomSequence(0).take(50).any { it == LocalTime.MAX })
    }


    @Test
    fun generateGlobalMin() {
        assertTrue(Generator.localTimes().randomSequence(0).take(50).any { it == LocalTime.MIN })
    }

    @Test
    fun generateMax() {
        val max = LocalTime.NOON.plusHours(1)
        assertTrue(Generator.localTimes(max = max).randomSequence(0).take(50).any { it == max })
    }

    @Test
    fun generateMin() {
        val min = LocalTime.NOON.plusHours(1)
        assertTrue(Generator.localTimes(min = min).randomSequence(0).take(50).any { it == min })
    }
}
