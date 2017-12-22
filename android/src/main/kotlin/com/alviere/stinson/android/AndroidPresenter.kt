package com.alviere.stinson.android

import android.os.Bundle
import android.support.annotation.CallSuper
import com.alviere.stinson.*

abstract class AndroidPresenter<V : View, S : ParcelableState, E : Executor<*>>(stinson: Stinson<S, E>)
    : Presenter<V, S, E>(stinson) {

    @CallSuper
    fun onCreate(savedInstanceState: Bundle?) {
        val state = savedInstanceState?.getParcelable(KEY_STATE) ?: initialState()

        if (!stinson.isInitialized()) {
            stinson.initialize(this, state)
        }

        stinson.accept(Init)
    }

    fun onStart() {}
    fun onResume() {}
    fun onPause() {}
    fun onStop() {}

    @CallSuper
    fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putParcelable(KEY_STATE, stinson.state)
    }

    @CallSuper
    fun onDestroy() {
        stinson.dispose()
    }

    companion object {
        private const val KEY_STATE = "com.alviere.stinson.STATE"
    }
}
