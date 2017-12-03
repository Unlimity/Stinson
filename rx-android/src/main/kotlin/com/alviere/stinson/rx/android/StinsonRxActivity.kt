package com.alviere.stinson.rx.android

import android.app.Activity
import android.os.Bundle
import com.alviere.stinson.View

abstract class StinsonRxActivity<V : View, S : ParcelableState, out P : AndroidRxPresenter<V, S>>
    : Activity() {

    protected val view: V
    protected val presenter: P

    init {
        view = provideView()
        presenter = providePresenter()
    }

    protected abstract fun providePresenter(): P
    protected abstract fun provideView(): V

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.attach(view)
        presenter.onCreate()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState?.run { presenter.onRestoreInstanceState(this) }
    }

    override fun onStart() {
        super.onStart()

        if (!presenter.isAttached()) {
            presenter.attach(view)
        }

        presenter.onStart()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    override fun onStop() {
        super.onStop()

        if (presenter.isAttached()) {
            presenter.detach()
        }

        presenter.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.run { presenter.onSaveInstanceState(this) }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }
}
