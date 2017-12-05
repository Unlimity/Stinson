package com.alviere.stinson.rx

import com.alviere.stinson.Message
import com.alviere.stinson.Subscription
import io.reactivex.Observable

class RxSubscription<in P>(private val lambda: (P) -> Observable<Message>) : Subscription<P> {
    override fun create(params: P): Observable<Message> = lambda(params)
}
