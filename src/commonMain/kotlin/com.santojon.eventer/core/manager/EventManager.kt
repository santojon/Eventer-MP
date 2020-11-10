package com.santojon.eventer.core.manager

import com.noheltcj.rxcommon.subjects.PublishSubject
import com.santojon.eventer.core.stream.EventStream

/**
 * Used to manage events using [EventStream]
 */
open class EventManager<T : Any> {
    // Events subject
    protected var evs: PublishSubject<T>? = PublishSubject()

    /**
     * Add event to Subject
     */
    open fun addEvent(event: T?) = event?.let { evs?.onNext(event) }
    open fun publish(event: T?) = addEvent(event)
    open fun sendEvent(event: T?) = addEvent(event)

    /**
     * Add events to Subject
     */
    open fun addEvents(vararg events: T?) = events.forEach { event -> addEvent(event) }
    open fun publishMany(vararg events: T?) = addEvents(*events)
    open fun sendEvents(vararg events: T?) = addEvents(*events)

    /**
     * Return stream of events
     */
    open fun asStream(): EventStream<T>? = EventStream(evs)
    open val events: EventStream<T>? = asStream()
    open val stream: EventStream<T>? = asStream()

    /**
     * Clear the eventStream
     */
    open fun clear() {
        evs = null
        evs = PublishSubject()
    }
}