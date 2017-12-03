package com.alviere.stinson

sealed class AbstractCommand

open class Command : AbstractCommand()

class None : Command()
