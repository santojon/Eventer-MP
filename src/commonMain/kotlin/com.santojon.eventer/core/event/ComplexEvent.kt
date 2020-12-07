package com.santojon.eventer.core.event

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.buffer
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.observable.subscribe
import com.santojon.eventer.core.stream.EventStream
import com.badoo.reaktive.observable.merge

open class ComplexEvent(
    private val observable: Observable<Pair<Any, Int>>?,
    private val count: Int,
    private val skip: Int = 5
) {
    fun subscribe(onComplete: () -> Unit) {
        observable?.buffer(count, skip)
            ?.subscribe { bundle ->
                val events = bundle.listIterator()
                val values = mutableSetOf<Int?>()
                for (item in events) values.add(item.second)
                if (values.count() == count) onComplete()
            }
    }

    fun <E : Any> merge(eventStream: EventStream<E>?): ComplexEvent {
        val merged = merge(
            observable!!,
            eventStream?.observable?.map { element ->
                Pair(element, count.plus(1))
            }!!
        )
        return ComplexEvent(merged, count.plus(1))
    }
}