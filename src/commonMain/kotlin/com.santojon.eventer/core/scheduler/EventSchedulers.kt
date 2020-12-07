package com.santojon.eventer.core.scheduler

import com.badoo.reaktive.scheduler.*

/**
 * Used to provide [Scheduler]s for streams to be observed
 */
object EventSchedulers {
    // Constant values for schedulers
    const val IO: Int = 0
    const val COMPUTATION: Int = 1
    const val NEW_THREAD: Int = 2
    const val TRAMPOLINE: Int = 3
    const val SINGLE: Int = 4
    const val MAIN_THREAD: Int = 5

    // Schedulers to provide
    private val io: Scheduler by lazy { ioScheduler }
    private val computation: Scheduler by lazy { computationScheduler }
    private val newThread: Scheduler by lazy { newThreadScheduler }
    private val trampoline: Scheduler by lazy { trampolineScheduler }
    private val single: Scheduler by lazy { singleScheduler }
    private val mainThread: Scheduler by lazy { mainScheduler }

    /**
     * Provide a [Scheduler] responding to given parameter(s)
     */
    fun from(schedulerRef: Int?): Scheduler {
        return when (schedulerRef) {
            IO -> io
            COMPUTATION -> computation
            NEW_THREAD -> newThread
            TRAMPOLINE -> trampoline
            SINGLE -> single
            MAIN_THREAD -> mainThread
            else -> io
        }
    }
}