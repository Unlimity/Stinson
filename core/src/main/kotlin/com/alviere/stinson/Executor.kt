package com.alviere.stinson

interface Executor<out T> {
    fun execute(): T
}