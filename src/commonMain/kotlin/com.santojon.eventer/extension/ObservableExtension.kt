package com.santojon.eventer.extension

import com.noheltcj.rxcommon.Source
import com.noheltcj.rxcommon.observers.Observer

fun <T> Source<T>?.subscribe(
    onNext: (T?) -> Unit,
    onError: (Throwable?) -> Unit,
    onComplete: () -> Unit
) = this?.subscribe(object : Observer<T> {
    override fun onComplete() = onComplete()
    override fun onError(throwable: Throwable) = onError(throwable)
    override fun onNext(value: T) = onNext(value)
})