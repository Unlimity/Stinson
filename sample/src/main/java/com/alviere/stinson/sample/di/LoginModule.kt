package com.alviere.stinson.sample.di

import com.alviere.stinson.rx.RxStinson
import com.alviere.stinson.sample.presenter.LoginPresenter
import com.alviere.stinson.sample.state.LoginState
import io.reactivex.android.schedulers.AndroidSchedulers
import org.koin.android.module.AndroidModule

class LoginModule : AndroidModule() {
    override fun context() = applicationContext {
        context(CONTEXT_NAME) {
            provide {
                RxStinson<LoginState>(AndroidSchedulers.mainThread())
            }

            provide {
                LoginPresenter(get())
            }
        }
    }

    companion object {
        const val CONTEXT_NAME = "LoginActivity"
    }
}