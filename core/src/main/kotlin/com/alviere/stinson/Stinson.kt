package com.alviere.stinson

import java.util.*

abstract class Stinson<S : State, E : Executor> {
    protected lateinit var component: Component<S, E>
    protected lateinit var state: S

    protected val queue = ArrayDeque<Message>()

    abstract fun init(component: Component<S, E>, state: S): Any
    abstract fun accept(message: Message)
}
