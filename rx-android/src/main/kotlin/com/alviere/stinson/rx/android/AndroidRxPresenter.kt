package com.alviere.stinson.rx.android

import android.os.Bundle
import com.alviere.stinson.View
import com.alviere.stinson.rx.RxPresenter
import com.alviere.stinson.rx.RxStinson

abstract class AndroidRxPresenter<in V : View, S : ParcelableState>(stinson: RxStinson<S>)
    : RxPresenter<V, S>(stinson) {

    fun onCreate() {}
    fun onRestoreInstanceState(savedInstanceState: Bundle) {}
    fun onStart() {}
    fun onResume() {}
    fun onPause() {}
    fun onStop() {}
    fun onSaveInstanceState(savedInstanceState: Bundle) {}
    fun onDestroy() {}
}
