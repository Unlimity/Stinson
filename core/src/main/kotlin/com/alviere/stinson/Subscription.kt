package com.alviere.stinson

interface Subscription<in P> {
    fun create(params: P): Any
}
