package com.alviere.stinson

interface Component<S : State, out E: Executor> {
    fun update(message: Message, state: S): Pair<Command, S>
    fun render(state: S)
    fun executor(command: Command): E
}
