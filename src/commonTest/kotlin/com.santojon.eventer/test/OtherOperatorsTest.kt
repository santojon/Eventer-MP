package com.santojon.eventer.test

import com.santojon.eventer.core.event.Event
import com.santojon.eventer.core.stream.EventStream
import com.santojon.eventer.event.IntEvent
import com.santojon.eventer.event.StringEvent
import com.santojon.eventer.simulator.EventStreamSimulator
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests of Many other Operators
 */
class OtherOperatorsTest {
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
     * Verify return only a selected window of events
     */
    @Test
    fun testWindow() {
        val events = listOf<Event?>(
            StringEvent("10"),
            IntEvent(10),
            StringEvent("5"),
            StringEvent("12"),
            IntEvent(12)
        )

        val expected = listOf<Event?>(
            StringEvent("10"),
            IntEvent(10),
            StringEvent("5")
        )

        assertEquals(expected, simulator.simulate(events, ::windowFunction))
    }

    /**
     * Verify return only events in a sequence
     */
    @Test
    fun testSequence() {
        val events = listOf(
            IntEvent(10),
            IntEvent(12),
            IntEvent(5)
        )

        val expected = listOf(
            IntEvent(10),
            IntEvent(12)
        )

        assertEquals(expected, simulator.simulateCompare(events, ::sequenceFunction))
    }

    /**
     * Get all events of a stream
     */
    @Test
    fun testAccumulator() {
        val events = listOf(
            IntEvent(10),
            IntEvent(12),
            IntEvent(5)
        )

        val expected = listOf(
            IntEvent(10),
            IntEvent(12),
            IntEvent(5)
        )

        assertEquals(expected, simulator.simulateCompare(events, ::accumulatorFunction))
    }

    /**
     * Map all events of a stream to another with duplicated values
     */
    @Test
    fun testMap() {
        val events = listOf(
            IntEvent(10),
            IntEvent(12),
            IntEvent(5)
        )

        val expected = listOf(
            IntEvent(10 * 2),
            IntEvent(12 * 2),
            IntEvent(5 * 2)
        )

        assertEquals(expected, simulator.simulate(events, ::mapFunction))
    }

    /**
     * Filter all events of a stream with value defined by predicate
     */
    @Test
    fun testFilter() {
        val events = listOf(
            IntEvent(10),
            IntEvent(12),
            IntEvent(5)
        )

        val expected = listOf(IntEvent(12))

        assertEquals(expected, simulator.simulate(events, ::filterFunction))
    }

    /*****************************************************
     *
     * Functions to simulate
     *
     *****************************************************/

    /**
     * Filter the stream for an event quantity window
     */
    private fun windowFunction(stream: EventStream<Event>?): EventStream<Event>? =
        stream?.window(3, 0)

    /**
     * Filter the stream for an event sequence
     */
    private fun sequenceFunction(stream: EventStream<Event>?): EventStream<List<Event>>? =
        stream?.sequence(2, 0) { a, b ->
            (a as? IntEvent?)?.value ?: 0 > (b as? IntEvent?)?.value ?: 0
        }

    /**
     * Filter the stream for an event sequence
     */
    private fun accumulatorFunction(
        stream: EventStream<Event>?
    ): EventStream<MutableList<Event>>? = stream?.accumulator()

    /**
     * Map stream to another
     */
    private fun mapFunction(
        stream: EventStream<Event>?
    ): EventStream<Event>? = stream?.map { IntEvent((it as IntEvent).value * 2) }

    /**
     * Filter stream for value
     */
    private fun filterFunction(
        stream: EventStream<Event>?
    ): EventStream<Event>? = stream?.filter { (it as IntEvent).value > 10 }

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