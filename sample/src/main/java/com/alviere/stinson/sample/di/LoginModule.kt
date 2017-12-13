package com.alviere.stinson.sample.di

import com.alviere.stinson.sample.presenter.LoginPresenter
import org.koin.android.module.AndroidModule

class LoginModule : AndroidModule() {
    override fun context() = applicationContext {
        context(CONTEXT_NAME) {
            provide {
                LoginPresenter()
            }
        }
    }

    companion object {
        const val CONTEXT_NAME = "LoginActivity"
    }
}