package com.alviere.stinson.rx

import com.alviere.stinson.Message
import com.alviere.stinson.Subscription
import io.reactivex.Observable

class RxSubscription<in P>(private val lambda: (P) -> Observable<out Message>)
    : Subscription<P, Observable<Message>> {

    private var subscription: Observable<Message>? = null
    private var parameters: P? = null

    override fun request(params: P): Observable<Message>? {
        return if (subscription == null || params != parameters) {
            create(params).apply {
                subscription = this
                parameters = params
            }
        } else null
    }

    override fun create(params: P): Observable<Message> = lambda(params) as Observable<Message>
}
