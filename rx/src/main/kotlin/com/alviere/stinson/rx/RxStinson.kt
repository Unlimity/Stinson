package com.alviere.stinson.rx

import com.alviere.stinson.*
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

class RxStinson<S : State>(private val observeScheduler: Scheduler) : Stinson<S, RxExecutor>() {
    private val subject = BehaviorSubject.create<Pair<Message, S>>()
    private val subscriptions = mutableMapOf<String, Disposable>()

    private lateinit var scheduler: Disposable

    override fun initialize(component: Component<S, RxExecutor>, state: S) {
        this.component = component
        this.state = state

        component.subscribe(state)

        scheduler = subject
                .observeOn(observeScheduler)
                .map { (message, state) ->
                    component.update(message, state)
                }
                .doOnNext { (_, state) ->
                    this.state = state

                    if (queue.isNotEmpty()) {
                        queue.removeFirst()
                    }

                    component.render(state)
                    component.subscribe(state)
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
                .observeOn(observeScheduler)
                .subscribe { message ->
                    when (message) {
                        is Idle -> {
                        } // Do nothing
                        else -> queue.addLast(message)
                    }

                    loop()
                }
    }

    override fun isInitialized() = ::scheduler.isInitialized && !scheduler.isDisposed

    override fun accept(message: Message) {
        queue.addLast(message)

        if (queue.size == 1) {
            next()
        }
    }

    override fun <P> subscribe(subscription: Subscription<P>, params: P) {
        if (subscription !is RxSubscription) {
            throw IllegalArgumentException("You can't pass non RxSubscription to RxStinson!")
        }

        val key = subscription.javaClass.canonicalName

        subscription.request(params)?.run {
            subscriptions[key]?.run { if (!isDisposed) dispose() }
            subscriptions.put(key, this.subscribe { accept(it) })
        }
    }

    override fun dispose() {
        scheduler.takeIf { !it.isDisposed }?.dispose()
        subscriptions.forEach { (_, value) -> if (!value.isDisposed) value.dispose() }
        subscriptions.clear()
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
