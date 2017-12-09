package com.alviere.stinson

interface Subscription<in P> {
    fun request(params: P): Any?
    fun create(params: P): Any
}
