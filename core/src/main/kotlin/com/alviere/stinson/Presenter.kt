package com.alviere.stinson

abstract class Presenter<V : View, S : State, E : Executor<*>>(protected val stinson: Stinson<S, E>)
    : Component<S, E> {

    protected var view: V? = null

    fun attach(view: V) {
        this.view = view
    }

    fun detach() {
        this.view = null
    }

    fun isAttached() = view != null

    abstract fun initialState(): S
}
