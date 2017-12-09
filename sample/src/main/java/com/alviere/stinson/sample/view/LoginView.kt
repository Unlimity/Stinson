package com.alviere.stinson.sample.view

import android.support.annotation.StringRes
import com.alviere.stinson.View

interface LoginView : View {
    fun setLoginText(text: String)
    fun setLoginErrorText(@StringRes text: Int)
    fun setLoginErrorEnabled(enabled: Boolean)

    fun setPasswordText(text: String)
    fun setPasswordErrorText(@StringRes text: Int)
    fun setPasswordErrorEnabled(enabled: Boolean)

    fun setLoading(loading: Boolean)

    fun showSnackbar(@StringRes text: Int)
}