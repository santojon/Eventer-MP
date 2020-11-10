package com.santojon.eventer.core.scheduler

/**
 * Used to provide [Scheduler]s for streams to be observed
 */
actual object EventSchedulers {
    actual val IO: Int
        get() = TODO("Not yet implemented")
    actual val COMPUTATION: Int
        get() = TODO("Not yet implemented")
    actual val NEW_THREAD: Int
        get() = TODO("Not yet implemented")
    actual val TRAMPOLINE: Int
        get() = TODO("Not yet implemented")
    actual val SINGLE: Int
        get() = TODO("Not yet implemented")
    actual val MAIN_THREAD: Int
        get() = TODO("Not yet implemented")
    actual val io: Scheduler
        get() = TODO("Not yet implemented")
    actual val computation: Scheduler
        get() = TODO("Not yet implemented")
    actual val newThread: Scheduler
        get() = TODO("Not yet implemented")
    actual val trampoline: Scheduler
        get() = TODO("Not yet implemented")
    actual val single: Scheduler
        get() = TODO("Not yet implemented")
    actual val mainThread: Scheduler
        get() = TODO("Not yet implemented")

    /**
     * Provide a [Scheduler] responding to given parameter(s)
     */
    actual fun from(schedulerRef: Int?): Scheduler {
        TODO("Not yet implemented")
    }

}