package com.alviere.stinson.rx

import com.alviere.stinson.Presenter
import com.alviere.stinson.State
import com.alviere.stinson.View
import io.reactivex.Scheduler

abstract class RxPresenter<V : View, S : State>(observeScheduler: Scheduler)
    : Presenter<V, S, RxExecutor>(RxStinson<S>(observeScheduler))
