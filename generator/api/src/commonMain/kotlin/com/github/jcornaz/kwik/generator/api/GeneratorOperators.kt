package com.github.jcornaz.kwik.generator.api

/**
 * Returns a generator containing the results of applying the given transform function to each element emitted by
 * the original generator.
 */
fun <T, R> Generator<T>.map(transform: (T) -> R): Generator<R> =
    MapGenerator(this, transform)

private class MapGenerator<T, R>(private val source: Generator<T>, private val transform: (T) -> R) :
    Generator<R> {
    override val samples: Set<R> = source.samples.mapTo(mutableSetOf(), transform)

    override fun randoms(seed: Long): Sequence<R> = source.randoms(seed).map(transform)
}

/**
 * Returns a generator containing only elements matching the given predicate.
 *
 * **Usage of this operator slows down the property tests**
 * Use it with caution and always favor customizing or creating generators if possible.
 */
fun <T> Generator<T>.filter(predicate: (T) -> Boolean): Generator<T> =
    FilterGenerator(this, predicate)

/**
 * Returns a generator containing all elements except the ones matching the given predicate.
 *
 * **Usage of this operator slows down the property tests**
 * Use it with caution and always favor customizing or creating generators if possible.
 */
fun <T> Generator<T>.filterNot(predicate: (T) -> Boolean): Generator<T> =
    FilterGenerator(this) { !predicate(it) }

private class FilterGenerator<T>(
    private val source: Generator<T>,
    private val predicate: (T) -> Boolean
) : Generator<T> {
    override val samples: Set<T> = source.samples.filterTo(mutableSetOf(), predicate)

    override fun randoms(seed: Long): Sequence<T> = source.randoms(seed).filter(predicate)
}

/**
 * Returns a new generator adding the given [samples] into generated random values.
 *
 * The "random" values always start by the given [samples] so that they always appear at least once.
 */
fun <T> Generator<T>.withSamples(vararg samples: T): Generator<T> =
    withSamples(samples.asList())

/**
 * Returns a new generator adding the given [samples] into generated random values.
 *
 * The "random" values always start by the given [samples] so that they always appear at least once.
 */
fun <T> Generator<T>.withSamples(samples: Iterable<T>): Generator<T> {
    val sampleList = (samples as? List<T>) ?: samples.toList()
    if (sampleList.isEmpty()) return this

    return SampleGenerator(this, sampleList)
}

/**
 * Returns a new generator adding `null` into generated random values.
 *
 * The "random" values always start by `null` so that it always appear at least once.
 */
fun <T> Generator<T>.withNull(): Generator<T?> =
    NullGenerator(this)

private class SampleGenerator<T>(
    private val source: Generator<T>,
    samples: Iterable<T>
) : Generator<T> {

    override val samples: Set<T> = source.samples + samples

    init {
        require(this.samples.isNotEmpty()) { "No sample provided" }
    }

    override fun randoms(seed: Long): Sequence<T> = source.randoms(seed)
}

private class NullGenerator<T>(private val source: Generator<T>) :
    Generator<T?> {
    override val samples: Set<T?> = source.samples.plus<T?>(null)

    override fun randoms(seed: Long): Sequence<T?> = source.randoms(seed)
}