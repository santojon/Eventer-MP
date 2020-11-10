package com.santojon.eventer.core.stream

import com.noheltcj.rxcommon.Source
import com.santojon.eventer.core.event.ListEvent
import com.santojon.eventer.extension.subscribe
import kotlin.reflect.KClass

open class EventStream<T : Any>(open val observable: Source<T>?) {
    /**
     * Subscribe to get stream
     */
    open fun subscribe(
        onNext: (T?) -> Unit,
        onError: (Throwable?) -> Unit,
        onComplete: () -> Unit
    ) {
        observable?.subscribe(onNext, onError, onComplete)
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
    inline fun <reified R : Any> isAs(): EventStream<R>? {
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
    inline fun <reified R : Any> isAs(crossinline comparator: ((R?) -> Boolean)): EventStream<R>? {
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
    inline fun <reified R : Iterable<K>, reified K : Any> isIterableAs(): EventStream<R>? {
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
    inline fun <reified R : ListEvent<K>, reified K : Any> isListEventOf(): EventStream<R>? {
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
}