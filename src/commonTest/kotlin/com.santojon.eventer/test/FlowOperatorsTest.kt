package com.santojon.eventer.test

import com.santojon.eventer.core.stream.EventStream
import com.santojon.eventer.event.IntEvent
import com.santojon.eventer.simulator.EventStreamSimulator
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests of CEP operators related to Flow Management
 */
class FlowOperatorsTest {
    /*****************************************************
     *
     * Initialization
     *
     *****************************************************/

    /**
     * Simulator instance
     */
    private lateinit var simulator: EventStreamSimulator<IntEvent>

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
     * Verify return distinct Events from source
     */
    @Test
    fun testDistinctEvents() {
        val events = listOf(
            IntEvent(10),
            IntEvent(10),
            IntEvent(5),
            IntEvent(12),
            IntEvent(12)
        )

        val expected = listOf(
            IntEvent(10),
            IntEvent(5),
            IntEvent(12)
        )

        assertEquals(expected, simulator.simulate(events, ::distinctFunction))
    }

    /**
     * Verify return distinct Events from source with a custom comparator
     */
    @Test
    fun testDistinctWithComparatorEvents() {
        val events = listOf(
            IntEvent(10),
            IntEvent(10),
            IntEvent(5),
            IntEvent(12),
            IntEvent(12)
        )

        val expected = listOf(
            IntEvent(10),
            IntEvent(10),
            IntEvent(12),
            IntEvent(12)
        )

        assertEquals(expected, simulator.simulate(events, ::distinctWithComparatorFunction))
    }

    /**
     * Verify two sources to give only Events existent only in first one
     */
    @Test
    fun testExceptEvents() {
        val events1 = listOf(
            IntEvent(10),
            IntEvent(10),
            IntEvent(5),
            IntEvent(12),
            IntEvent(12)
        )

        val events2 = listOf(
            IntEvent(10),
            IntEvent(10),
            IntEvent(12),
            IntEvent(12)
        )

        val expected = listOf(
            IntEvent(
                5
            )
        )
        assertEquals(expected, simulator.simulate(events1, events2, ::exceptFunction))
    }

    /**
     * Verify two sources to give Events existent in both with no duplicates
     */
    @Test
    fun testIntersectEvents() {
        val events1 = listOf(
            IntEvent(10),
            IntEvent(10),
            IntEvent(5),
            IntEvent(12),
            IntEvent(12)
        )

        val events2 = listOf(
            IntEvent(10),
            IntEvent(10),
            IntEvent(12),
            IntEvent(12)
        )

        val expected = listOf(
            IntEvent(10),
            IntEvent(12)
        )

        assertEquals(expected, simulator.simulate(events1, events2, ::intersectFunction))
    }

    /**
     * Unify two streams into one
     */
    @Test
    fun testUnionEvents() {
        val events1 = listOf(
            IntEvent(10),
            IntEvent(5),
            IntEvent(12)
        )

        val events2 = listOf(
            IntEvent(10),
            IntEvent(12)
        )

        val expected = listOf(
            IntEvent(10),
            IntEvent(12),
            IntEvent(10),
            IntEvent(5),
            IntEvent(12)
        )

        assertEquals(expected, simulator.simulate(events1, events2, ::unionFunction))
    }

    /**
     * Order Events in source by comparison
     */
    @Test
    fun testOrderEvents() {
        val events = listOf(
            IntEvent(10),
            IntEvent(11),
            IntEvent(5),
            IntEvent(1),
            IntEvent(12)
        )

        val expected = listOf(
            IntEvent(1),
            IntEvent(5),
            IntEvent(10),
            IntEvent(11),
            IntEvent(12)
        )

        assertEquals(expected, simulator.simulateCompare(events, ::orderByFunction))
    }

    /**
     * Order Events in source by comparison
     */
    @Test
    fun testGroupEvents() {
        val events = listOf(
            IntEvent(10),
            IntEvent(10),
            IntEvent(5),
            IntEvent(12),
            IntEvent(12)
        )

        val expected = mapOf(
            Pair(
                10, listOf(
                    IntEvent(10),
                    IntEvent(10)
                )
            ),
            Pair(
                5, listOf(
                    IntEvent(5)
                )
            ),
            Pair(
                12, listOf(
                    IntEvent(12),
                    IntEvent(12)
                )
            )
        )

        assertEquals(expected, simulator.simulate(events, ::groupByFunction))
    }


    /*****************************************************
     *
     * Functions to simulate
     *
     *****************************************************/

    /**
     * Filter the distinct Events
     */
    private fun distinctFunction(stream: EventStream<IntEvent>?): EventStream<IntEvent>? =
        stream?.distinct()

    /**
     * Filter the distinct Events with a comparator
     */
    private fun distinctWithComparatorFunction(
        stream: EventStream<IntEvent>?
    ): EventStream<IntEvent>? = stream?.distinct { a, b ->
        a.value > b.value
    }

    /**
     * Executes Except operator
     */
    private fun exceptFunction(
        stream1: EventStream<IntEvent>?,
        stream2: EventStream<IntEvent>?
    ): EventStream<IntEvent>? = stream1?.not(stream2)

    /**
     * Executes Intersect operator
     */
    private fun intersectFunction(
        stream1: EventStream<IntEvent>?,
        stream2: EventStream<IntEvent>?
    ): EventStream<IntEvent>? = stream1?.intersect(stream2)

    /**
     * Executes Union operator
     */
    private fun unionFunction(
        stream1: EventStream<IntEvent>?,
        stream2: EventStream<IntEvent>?
    ): EventStream<IntEvent>? = stream1?.union(stream2)

    /**
     * Executes OrderBy operator
     */
    private fun orderByFunction(stream: EventStream<IntEvent>?): EventStream<List<IntEvent>>? =
        stream?.orderBy { event -> event?.value }

    /**
     * Executes GroupBy operator
     */
    private fun groupByFunction(stream: EventStream<IntEvent>?): EventStream<Map<Int?, List<IntEvent>>>? =
        stream?.groupBy { event -> event?.value }

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