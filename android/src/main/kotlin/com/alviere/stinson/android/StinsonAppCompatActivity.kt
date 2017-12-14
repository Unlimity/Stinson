package com.alviere.stinson.android

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.alviere.stinson.View

abstract class StinsonAppCompatActivity<V : View, S : ParcelableState, out P : AndroidPresenter<V, S, *>>
    : AppCompatActivity() {

    private lateinit var view: V
    private lateinit var presenter: P

    protected abstract fun providePresenter(): P
    protected abstract fun provideView(): V

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = provideView()
        presenter = providePresenter()

        presenter.attach(view)
        presenter.onCreate(savedInstanceState)
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

        if (isFinishing) {
            presenter.onDestroy()
        }
    }
}
