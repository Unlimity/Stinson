package com.alviere.stinson

sealed class AbstractCommand

open class Command : AbstractCommand()

object None : Command()
