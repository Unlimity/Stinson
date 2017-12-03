package com.alviere.stinson.rx

import com.alviere.stinson.*
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

class RxStinson<S : State>(private val scheduler: Scheduler) : Stinson<S, RxExecutor>() {
    private val subject = BehaviorSubject.create<Pair<Message, S>>()

    override fun init(component: Component<S, RxExecutor>, state: S): Disposable {
        this.component = component
        this.state = state

        return subject
                .map { (message, state) ->
                    component.update(message, state)
                }
                .observeOn(scheduler)
                .doOnNext { (_, state) ->
                    component.render(state)
                }
                .doOnNext { (_, state) ->
                    this.state = state

                    if (queue.isNotEmpty()) {
                        queue.removeFirst()
                    }

                    loop()
                }
                .filter { (command, _) -> command !is None }
                .observeOn(Schedulers.io())
                .flatMap { (command, _) ->
                    component.executor(command)
                            .execute()
                            .onErrorResumeNext { error -> Single.just(Error(error, command)) }
                            .toObservable()
                }
                .observeOn(scheduler)
                .subscribe { message ->
                    when (message) {
                        is Idle -> {
                        } // Do nothing
                        else -> queue.addLast(message)
                    }

                    loop()
                }
    }

    override fun accept(message: Message) {
        queue.addLast(message)

        if (queue.size == 1) {
            next()
        }
    }

    private fun loop() {
        if (queue.isNotEmpty()) {
            next()
        }
    }

    private fun next() {
        subject.onNext(Pair(queue.first, state))
    }
}
