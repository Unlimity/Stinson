package com.alviere.stinson.rx.android

import android.os.Bundle
import android.support.annotation.CallSuper
import com.alviere.stinson.Init
import com.alviere.stinson.View
import com.alviere.stinson.rx.RxPresenter
import com.alviere.stinson.rx.RxStinson
import io.reactivex.disposables.Disposable

abstract class AndroidRxPresenter<V : View, S : ParcelableState>(stinson: RxStinson<S>)
    : RxPresenter<V, S>(stinson) {

    private lateinit var subsription: Disposable

    @CallSuper
    fun onCreate(savedInstanceState: Bundle?) {
        val state = savedInstanceState?.getParcelable(KEY_STATE) ?: initialState()
        subsription = stinson.init(this, state) as Disposable
        stinson.accept(Init())
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

        if (!subsription.isDisposed) {
            subsription.dispose()
        }
    }

    companion object {
        private const val KEY_STATE = "com.alviere.stinson.STATE"
    }
}
