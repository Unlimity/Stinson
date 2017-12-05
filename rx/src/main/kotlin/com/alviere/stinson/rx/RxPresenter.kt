package com.alviere.stinson.rx

import com.alviere.stinson.Presenter
import com.alviere.stinson.State
import com.alviere.stinson.View

abstract class RxPresenter<V : View, S : State>(stinson: RxStinson<S>)
    : Presenter<V, S, RxExecutor>(stinson)
