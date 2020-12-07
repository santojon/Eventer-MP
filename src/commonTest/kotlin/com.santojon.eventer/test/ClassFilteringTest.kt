package com.santojon.eventer.test

import com.santojon.eventer.core.event.Event
import com.santojon.eventer.core.event.ListEvent
import com.santojon.eventer.core.stream.EventStream
import com.santojon.eventer.event.ExtendedListEvent
import com.santojon.eventer.event.IntEvent
import com.santojon.eventer.event.StringEvent
import com.santojon.eventer.event.extendedListEventOf
import com.santojon.eventer.simulator.EventStreamSimulator
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests of Class filtering functions
 */
class ClassFilteringTest {
    /*****************************************************
     *
     * Initialization
     *
     *****************************************************/

    /**
     * Simulator instance
     */
    private lateinit var simulator: EventStreamSimulator<Event>

    /**
     * Runs before each test runs
     */
    @BeforeTest
    fun setUp() {
        simulator = EventStreamSimulator()
    }

    /*****************************************************
     *
     * Tests
     *
     *****************************************************/

    /**
     * Verify return distinct [StringEvent] class from source
     */
    @Test
    fun testIsAs() {
        val events = listOf<Event?>(
            StringEvent("10"),
            IntEvent(10),
            StringEvent("5"),
            StringEvent("12"),
            IntEvent(12)
        )

        val expected = listOf(
            StringEvent("10"),
            StringEvent("5"),
            StringEvent("12")
        )

        assertEquals(expected, simulator.simulate(events, ::isAsStringEvent))
    }

    /**
     * Verify return distinct [IntEvent] class from source with comparator
     */
    @Test
    fun testIsAsWithComparator() {
        val events = listOf<Event?>(
            StringEvent("10"),
            IntEvent(10),
            StringEvent("5"),
            StringEvent("12"),
            IntEvent(12)
        )

        val expected = listOf(IntEvent(12))

        assertEquals(expected, simulator.simulate(events, ::isAsIntEventCompare))
    }

    /**
     * Verify return distinct [ListEvent]s of [StringEvent] class from source
     */
    @Test
    fun testIsIterableAs() {
        val events = listOf<Event?>(
            StringEvent("10"),
            IntEvent(10),
            StringEvent("5"),
            StringEvent("12"),
            IntEvent(12),
            extendedListEventOf(
                listOf(
                    StringEvent("L1"),
                    StringEvent("L2")
                )
            )
        )

        val expected = listOf(
            extendedListEventOf(
                listOf(
                    StringEvent("L1"),
                    StringEvent("L2")
                )
            )
        )

        assertEquals(expected, simulator.simulate(events, ::isIterableAsStringEventList))
    }

    /**
     * Verify return distinct [ListEvent]s of [StringEvent] class from source
     * ensure failing when sending empty list
     */
    @Test
    fun testIsIterableAsEmptyList() {
        val events = listOf<Event?>(
            StringEvent("10"),
            IntEvent(10),
            StringEvent("5"),
            StringEvent("12"),
            IntEvent(12),
            extendedListEventOf<StringEvent>()
        )

        // Ensure list of received events is empty (the list isn't received)
        val expected = listOf<ExtendedListEvent<StringEvent>?>()

        assertEquals(expected, simulator.simulate(events, ::isIterableAsStringEventList))
    }

    /**
     * Verify return distinct [ListEvent]s of [StringEvent] class from source
     * using function that validates empty lists element items type
     */
    @Test
    fun testIsListEventOf() {
        val events = listOf<Event?>(
            StringEvent("10"),
            IntEvent(10),
            StringEvent("5"),
            StringEvent("12"),
            IntEvent(12),
            extendedListEventOf<StringEvent>()
        )

        val expected = listOf(extendedListEventOf<StringEvent>())

        assertEquals(expected, simulator.simulate(events, ::isListEventOfStringEventList))
    }

    /**
     * Verify return distinct [Event]s of given classes from source
     */
    @Test
    fun testIsAnyOf() {
        val events = listOf<Event?>(
            StringEvent("10"),
            IntEvent(10),
            StringEvent("5"),
            StringEvent("12"),
            IntEvent(12),
            extendedListEventOf<StringEvent>()
        )

        val expected = listOf(
            IntEvent(10),
            IntEvent(12),
            extendedListEventOf<StringEvent>()
        )

        assertEquals(expected, simulator.simulate(events, ::isAnyOf))
    }

    /**
     * Verify not returning distinct [Event]s of given classes from source
     */
    @Test
    fun testIsNotAnyOf() {
        val events = listOf<Event?>(
            StringEvent("10"),
            StringEvent("5"),
            StringEvent("12")
        )

        val expected = listOf<Event?>()

        assertEquals(expected, simulator.simulate(events, ::isAnyOf))
    }

    /*****************************************************
     *
     * Functions to simulate
     *
     *****************************************************/

    /**
     * Filter the stream for [StringEvent] Class
     */
    private fun isAsStringEvent(stream: EventStream<Event>?): EventStream<StringEvent>? =
        stream?.isAs()

    /**
     * Filter the stream for [IntEvent] Class for values higher than 10
     */
    private fun isAsIntEventCompare(stream: EventStream<Event>?): EventStream<IntEvent>? =
        stream?.isAs { it?.value!! > 10 }

    /**
     * Filter the stream for [ListEvent] of [StringEvent] Class
     */
    private fun isIterableAsStringEventList(stream: EventStream<Event>?): EventStream<ListEvent<StringEvent>>? =
        stream?.isIterableAs()

    /**
     * Filter the stream for [ListEvent] of [StringEvent] Class
     * using function that validates empty lists element items type
     */
    private fun isListEventOfStringEventList(stream: EventStream<Event>?): EventStream<ListEvent<StringEvent>>? =
        stream?.isListEventOf()

    /**
     * Filter the stream for given classes
     */
    private fun isAnyOf(stream: EventStream<Event>?): EventStream<Event>? =
        stream?.isAnyOf(IntEvent::class, ExtendedListEvent::class)

    /*****************************************************
     *
     * Cleanup functions
     *
     *****************************************************/

    /**
     * Runs after each test runs
     */
    @AfterTest
    fun tearDown() {
        simulator.clear()
    }
}