package com.alviere.stinson

abstract class Presenter<in V : View, S : State, E : Executor>(protected val stinson: Stinson<S, E>)
    : Component<S, E> {

    private var view: V? = null

    fun attach(view: V) {
        this.view = view
    }

    fun detach() {
        this.view = null
    }

    fun isAttached() = view != null
}
