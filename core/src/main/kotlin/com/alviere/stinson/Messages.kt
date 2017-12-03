package com.alviere.stinson

sealed class AbstractMessage

open class Message : AbstractMessage()

class Init : Message()
class Idle : Message()
class Error(val error: Throwable, val command: Command) : Message()
