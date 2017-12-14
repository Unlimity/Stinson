package com.alviere.stinson

import java.util.*

abstract class Stinson<S : State, E : Executor> {
    lateinit var component: Component<S, E>
    lateinit var state: S

    protected val queue = ArrayDeque<Message>()

    abstract fun initialize(component: Component<S, E>, state: S)
    abstract fun isInitialized(): Boolean

    abstract fun accept(message: Message)
    abstract fun <P> subscribe(subscription: Subscription<P>, params: P)

    abstract fun dispose()
}
