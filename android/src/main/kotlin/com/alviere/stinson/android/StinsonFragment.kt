package com.alviere.stinson.android

import android.app.Fragment
import android.os.Bundle
import com.alviere.stinson.View

abstract class StinsonFragment<V : View, S : ParcelableState, out P : AndroidPresenter<V, S, *>>
    : Fragment() {

    private lateinit var view: V
    private lateinit var presenter: P

    protected abstract fun providePresenter(): P
    protected abstract fun provideView(): V

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = provideView()
        presenter = providePresenter()

        presenter.onCreate(savedInstanceState)
    }

    override fun onViewCreated(v: android.view.View?, savedInstanceState: Bundle?) {
        super.onViewCreated(v, savedInstanceState)

        if (!presenter.isAttached()) {
            presenter.attach(view)
        }
    }

    override fun onStart() {
        super.onStart()
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

        if (isRemoving) {
            presenter.onDestroy()
        }
    }
}
