package com.alviere.stinson.rx

import com.alviere.stinson.Message
import com.alviere.stinson.Subscription
import io.reactivex.Observable

class RxSubscription<in P>(private val lambda: (P) -> Observable<Message>) : Subscription<P> {
    private var subscription: Observable<Message>? = null
    private var parameters: P? = null

    override fun request(params: P): Observable<Message>? {
        return subscription?.takeIf {
            params == parameters
        } ?: create(params).apply {
            subscription = this
            parameters = params
        }
    }

    override fun create(params: P): Observable<Message> = lambda(params)
}
