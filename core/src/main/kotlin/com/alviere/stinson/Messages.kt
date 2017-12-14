package com.alviere.stinson

sealed class AbstractMessage

open class Message : AbstractMessage()

object Init : Message()
object Idle : Message()

class Error(val error: Throwable, val command: Command) : Message()
