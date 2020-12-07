package com.santojon.eventer.core.manager

import com.badoo.reaktive.scheduler.Scheduler
import com.badoo.reaktive.subject.publish.PublishSubject
import com.santojon.eventer.core.scheduler.EventSchedulers
import com.santojon.eventer.core.stream.EventStream

/**
 * Used to manage events using [EventStream]
 */
open class EventManager<T : Any>() {
    protected var subscribeOn: Scheduler? = null
    protected var observeOn: Scheduler? = null

    /**
     * Alternative constructors
     */
    constructor(subscribeOn: Scheduler? = null, observeOn: Scheduler? = null) : this() {
        this.subscribeOn = subscribeOn
        this.observeOn = observeOn
    }

    constructor(subscribeOn: Int?, observeOn: Int?) : this(
        EventSchedulers.from(subscribeOn),
        EventSchedulers.from(observeOn)
    )

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
    open fun asStream(): EventStream<T>? = EventStream(evs, subscribeOn, observeOn)
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