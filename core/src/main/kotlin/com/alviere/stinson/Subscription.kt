package com.alviere.stinson

interface Subscription<in P, out T> {
    fun request(params: P): T?
    fun create(params: P): T
}
