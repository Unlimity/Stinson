package com.alviere.stinson.rx

import com.alviere.stinson.Executor
import com.alviere.stinson.Message
import io.reactivex.Single

class RxExecutor(private val lambda: () -> Single<out Message>) : Executor<Single<Message>> {
    override fun execute() = lambda() as Single<Message>
}
