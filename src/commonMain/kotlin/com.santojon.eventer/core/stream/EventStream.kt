package com.santojon.eventer.core.stream

import com.badoo.reaktive.observable.*
import com.badoo.reaktive.scheduler.Scheduler
import com.santojon.eventer.core.event.ComplexEvent
import com.santojon.eventer.core.event.ListEvent
import com.santojon.eventer.core.scheduler.EventSchedulers
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.ObservableObserver
import com.badoo.reaktive.observable.observeOn
import com.badoo.reaktive.observable.subscribeOn
import kotlin.reflect.KClass

open class EventStream<T : Any>(open val observable: Observable<T>?) {
    protected var subscribeOn: Scheduler? = null
    protected var observeOn: Scheduler? = null

    /**
     * Alternative constructors
     */
    constructor(
        observable: Observable<T>?,
        subscribeOn: Scheduler? = null,
        observeOn: Scheduler? = null
    ) : this(observable) {
        this.subscribeOn = subscribeOn
        this.observeOn = observeOn
    }

    constructor(observable: Observable<T>?, subscribeOn: Int?, observeOn: Int?) : this(
        observable,
        EventSchedulers.from(subscribeOn),
        EventSchedulers.from(observeOn)
    )

    /**
     * Subscribe to get stream
     */
    open fun subscribe(
        onNext: (T?) -> Unit,
        onError: (Throwable?) -> Unit,
        onComplete: () -> Unit
    ) {
        when (subscribeOn) {
            null -> {
                when (observeOn) {
                    null -> observable?.subscribe(onNext, onError, onComplete)
                    else -> observable?.observeOn(observeOn)?.subscribe(onNext, onError, onComplete)
                }
            }
            else -> {
                when (observeOn) {
                    null -> observable?.subscribeOn(subscribeOn)
                        ?.subscribe(onNext, onError, onComplete)
                    else -> observable?.subscribeOn(subscribeOn)?.observeOn(observeOn)
                        ?.subscribe(onNext, onError, onComplete)
                }
            }
        }
    }

    open fun subscribe(onNext: (T?) -> Unit, onError: (Throwable?) -> Unit) {
        subscribe(onNext, onError, {})
    }

    open fun subscribe(onNext: (T?) -> Unit, onComplete: () -> Unit) {
        subscribe(onNext, {}, onComplete)
    }

    open fun subscribe(onNext: (T?) -> Unit) {
        subscribe(onNext, {}, {})
    }

    open fun onReceive(
        onNext: (T?) -> Unit,
        onError: (Throwable?) -> Unit,
        onComplete: () -> Unit
    ) =
        subscribe(onNext, onError, onComplete)

    open fun onReceive(onNext: (T?) -> Unit, onError: (Throwable?) -> Unit) =
        subscribe(onNext, onError, {})

    open fun onReceive(onNext: (T?) -> Unit, onComplete: () -> Unit) =
        subscribe(onNext, {}, onComplete)

    open fun onReceive(onNext: (T?) -> Unit) = subscribe(onNext)

    /**
     * Filter and Map Events by Class
     */
    inline fun <reified R : Any> isAs(): EventStream<R> {
        return EventStream(
            filter {
                it is R?
            }?.map {
                it as R
            }?.observable
        )
    }

    /**
     * Filter and Map Events by Class with comparator
     */
    inline fun <reified R : Any> isAs(crossinline comparator: ((R?) -> Boolean)): EventStream<R> {
        return EventStream(
            filter {
                it is R?
            }?.filter {
                comparator(it as R?)
            }?.map {
                it as R
            }?.observable
        )
    }

    /**
     * Filter and Map Events by Class for not empty [Iterable]s
     */
    inline fun <reified R : Iterable<K>, reified K : Any> isIterableAs(): EventStream<R> {
        return EventStream(
            filter {
                when (it) {
                    is Iterable<*> -> {
                        it.filterIsInstance<K>().isNotEmpty()
                    }
                    else -> {
                        it is R?
                    }
                }
            }?.map {
                it as R
            }?.observable
        )
    }

    /**
     * Filter and Map Events by Class for [ListEvent]s
     */
    inline fun <reified R : ListEvent<K>, reified K : Any> isListEventOf(): EventStream<R> {
        return EventStream(
            filter {
                when (it) {
                    is ListEvent<*> -> {
                        if (it.isEmpty()) {
                            it.validType<K>()
                        } else {
                            it.filterIsInstance<K>().isNotEmpty()
                        }
                    }
                    else -> {
                        it is R?
                    }
                }
            }?.map {
                it as R
            }?.observable
        )
    }

    /**
     * Filter Events by vararg Classes
     */
    open fun isAnyOf(vararg args: KClass<out T>): EventStream<T>? {
        return EventStream(
            filter { event ->
                args.map { arg ->
                    event!!::class == arg
                }.any { it }
            }?.observable
        )
    }

    /**
     * Filter emits only events from an
     * EventStream that satisfies a predicate function.
     */
    open fun filter(predicate: (T?) -> Boolean): EventStream<T>? {
        return EventStream(observable?.filter(predicate))
    }

    /**
     * Map transforms an EventStream by creating
     * a new EventStream through a projection function.
     */
    open fun <R : Any> map(transform: (T?) -> R): EventStream<R>? {
        return EventStream(observable?.map(transform))
    }

    /**
     * Sequence emits only events that follows
     * a specified order within a set of events.
     *
     * Takes a predicate function as the sequence condition
     * and the length of the sequence to be considered.
     */
    open fun sequence(
        count: Int,
        skip: Int = 0,
        predicate: (T?, T?) -> Boolean
    ): EventStream<List<T>>? {
        val sequenceEquals = observable
            ?.buffer(count, skip)
            ?.filter {
                var filter = true
                if (count > 1) {
                    for (i in 1 until (it.size - 1)) {
                        if (!predicate.invoke(it[i - 1], it[i])) {
                            filter = false
                            break
                        }
                    }
                }
                filter
            }

        return EventStream(sequenceEquals)
    }

    /**
     * Merges two EventStreams and notifies
     * the subscriber through a ComplexEvent object when
     * both EventStreams happen within a given time frame.
     */
    open fun <R : Any> merge(stream: EventStream<R>?): ComplexEvent? {
        val merged = merge(
            observable?.map { element -> Pair(element, 1) }!!,
            stream?.observable?.map { element -> Pair(element, 2) }!!
        )

        return ComplexEvent(merged, 2)
    }

    /**
     * Window only emits events that happened within a given time frame.
     */
    open fun window(count: Int, skip: Int): EventStream<T>? {
        return EventStream(
            observable?.buffer(
                count, skip
            )?.flatMap { list ->
                observable { emitter ->
                    var skipCounter = 0
                    list.forEach { item ->
                        if (skipCounter > 0) {
                            skipCounter -= 1
                        } else {
                            emitter.onNext(item)
                            if (skip > 0) {
                                skipCounter = skip
                            }
                        }
                    }
                }
            })
    }

    /**
     * Union merges two EventStreams into one EventStream
     * that emits events from both streams as they arrive.
     */
    open fun union(stream: EventStream<T>?): EventStream<T>? {
        return EventStream(
            merge(
                observable!!,
                stream?.observable!!
            ).distinctUntilChanged()
        )
    }

    /**
     * Filters the accumulated from all EventStreams that
     * exists in current stream but not in given one.
     */
    open fun not(stream: EventStream<T>?): EventStream<T>? {
        val streamAccumulated: EventStream<MutableList<T>> =
            EventStream(stream?.accumulator()?.observable?.startWithValue(arrayListOf()))

        val filtered = observable?.withLatestFrom(streamAccumulated.observable!!) { t, u ->
            Pair(t, u)
        }?.filter { (event, accumulated) ->
            !accumulated.contains(event)
        }?.map {
            it.first
        }

        return EventStream(filtered)
    }

    /**
     * Filters the accumulated from all EventStreams
     * that exists in current stream and in given one.
     */
    open fun intersect(stream: EventStream<T>?): EventStream<T>? {
        val streamAccumulated: EventStream<MutableList<T>> =
            EventStream(stream?.accumulator()?.observable?.startWithValue(arrayListOf()))

        val filtered = observable?.withLatestFrom(streamAccumulated.observable!!) { t, u ->
            Pair(t, u)
        }?.filter { (event, accumulated) ->
            accumulated.contains(event)
        }?.map {
            it.first
        }?.distinctUntilChanged()

        return EventStream(filtered)
    }

    /**
     * Return a stream without duplicates.
     */
    open fun distinct(comparator: ((T, T) -> Boolean)? = null): EventStream<T>? {
        return if (comparator == null) EventStream(observable?.distinctUntilChanged())
        else EventStream(observable?.distinctUntilChanged(comparator))
    }

    /**
     * Compare events by given comparator and return
     * a stream with all of then ordered by accordingly.
     */
    open fun <R : Comparable<R>> orderBy(comparison: ((T?) -> R?)): EventStream<List<T>>? {
        val ordered = accumulator()?.map {
            it!!.sortedBy(comparison)
        }?.observable

        return EventStream(ordered)
    }

    /**
     * Compare events by given comparator and return
     * a stream with all of then grouped by accordingly.
     */
    open fun <R> groupBy(comparison: ((T?) -> R?)): EventStream<Map<R?, List<T>>>? {
        val grouped = accumulator()?.map {
            it!!.groupBy(comparison)
        }?.observable

        return EventStream(grouped)
    }

    /**
     * Scans the observable to get an accumulation of events as a List Stream.
     */
    open fun accumulator(): EventStream<MutableList<T>>? {
        val accumulator = observable?.scan(
            mutableListOf<T>(),
            { accumulated, item ->
                accumulated.add(item)
                accumulated
            }
        )
        return EventStream(accumulator)
    }
}

/***************************************************
 * Observable Extensions
 * Used for simplifications in subscribe functions
 ***************************************************/

private fun <T> Observable<T>?.subscribe(
    onNext: (T?) -> Unit,
    onError: ((Throwable?) -> Unit)? = {},
    onComplete: (() -> Unit)? = {}
) = this?.subscribe(object : ObservableObserver<T> {
    override fun onComplete() = onComplete?.invoke() ?: Unit
    override fun onError(error: Throwable) = onError?.invoke(error) ?: Unit
    override fun onNext(value: T) = onNext(value)
    override fun onSubscribe(disposable: Disposable) {
        // Do nothing
    }
})

private fun <T> Observable<T>?.subscribeOn(
    scheduler: Scheduler?
) = scheduler?.let { this?.subscribeOn(it) }

private fun <T> Observable<T>?.observeOn(
    scheduler: Scheduler?
) = scheduler?.let { this?.observeOn(it) }