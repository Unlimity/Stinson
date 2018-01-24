package com.alviere.stinson.android

import android.os.Bundle
import android.support.annotation.CallSuper
import com.alviere.stinson.*

abstract class AndroidPresenter<V : View, S : ParcelableState, E : Executor<*>>(stinson: Stinson<S, E>)
    : Presenter<V, S, E>(stinson) {

    @CallSuper
    open fun onCreate(savedInstanceState: Bundle?) {
        val state = savedInstanceState?.getParcelable(KEY_STATE) ?: initialState()

        if (!stinson.isInitialized()) {
            stinson.initialize(this, state)
        }

        stinson.accept(Init)
    }

    open fun onStart() {}
    open fun onResume() {}
    open fun onPause() {}
    open fun onStop() {}

    @CallSuper
    open fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putParcelable(KEY_STATE, stinson.state)
    }

    @CallSuper
    open fun onDestroy() {
        stinson.dispose()
    }

    companion object {
        private const val KEY_STATE = "com.alviere.stinson.STATE"
    }
}
