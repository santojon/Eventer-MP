package com.santojon.eventer.core.manager

import com.noheltcj.rxcommon.subjects.PublishSubject
import com.santojon.eventer.core.stream.EventStream

/**
 * Used to manage events using [EventStream]
 */
class EventManager<T : Any> {
    // Events subject
    var evs: PublishSubject<T>? = PublishSubject()

    /**
     * Add event to Subject
     */
    fun addEvent(event: T?) = event?.let { evs?.onNext(event) }
    fun publish(event: T?) = addEvent(event)
    fun sendEvent(event: T?) = addEvent(event)

    /**
     * Add events to Subject
     */
    fun addEvents(vararg events: T?) = events.forEach { event -> addEvent(event) }
    fun publishMany(vararg events: T?) = addEvents(*events)
    fun sendEvents(vararg events: T?) = addEvents(*events)

    /**
     * Return stream of events
     */
    fun asStream(): EventStream<T>? = EventStream(evs)
    val events: EventStream<T>? = asStream()
    val stream: EventStream<T>? = asStream()

    /**
     * Clear the eventStream
     */
    fun clear() {
        evs = null
        evs = PublishSubject()
    }
}