package com.alviere.stinson.rx

import com.alviere.stinson.Executor
import com.alviere.stinson.Message
import io.reactivex.Single

class RxExecutor(val lambda: () -> Single<Message>) : Executor {
    override fun execute() = lambda()
}