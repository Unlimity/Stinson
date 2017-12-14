package com.alviere.stinson.sample.ui

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.alviere.stinson.android.StinsonActivity
import com.alviere.stinson.sample.R
import com.alviere.stinson.sample.di.LoginModule
import com.alviere.stinson.sample.presenter.LoginPresenter
import com.alviere.stinson.sample.state.LoginState
import com.alviere.stinson.sample.view.LoginView
import org.koin.android.ext.android.inject

import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.ext.android.releaseContext

class LoginActivity : StinsonActivity<LoginView, LoginState, LoginPresenter>(), LoginView {
    private val presenter by inject<LoginPresenter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        registerTextWatchers()

        passwordEdit.isPasswordVisibilityToggleEnabled = true
        loginButton.setOnClickListener { presenter.loginButtonClicked() }
    }

    override fun onPause() {
        super.onPause()

        if (isFinishing) {
            releaseContext(LoginModule.CONTEXT_NAME)
        }
    }

    override fun setLoginText(text: String) {
        loginEdit.editText?.takeIf { text != loginEdit.editText?.text.toString() }?.setText(text)
    }

    override fun setLoginErrorText(text: Int) {
        loginEdit.error = getString(text)
    }

    override fun setLoginErrorEnabled(enabled: Boolean) {
        loginEdit.isErrorEnabled = enabled
    }

    override fun setPasswordText(text: String) {
        passwordEdit.editText?.takeIf { text != passwordEdit.editText?.text.toString() }?.setText(text)
    }

    override fun setPasswordErrorText(text: Int) {
        passwordEdit.error = getString(text)
    }

    override fun setPasswordErrorEnabled(enabled: Boolean) {
        passwordEdit.isErrorEnabled = enabled
    }

    override fun setLoading(loading: Boolean) {
        loginEdit.isEnabled = !loading
        passwordEdit.isEnabled = !loading
        loginButton.isEnabled = !loading
        loginButton.text = if (loading) "" else getString(R.string.login_button_text)
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }

    override fun showSnackbar(text: Int) {
        Snackbar.make(contentView, text, Snackbar.LENGTH_LONG).show()
    }

    override fun providePresenter() = presenter

    override fun provideView() = this

    private fun registerTextWatchers() {
        loginEdit.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(text: Editable?) {
                text?.run { presenter.loginTextChanged(this.toString()) }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        passwordEdit.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(text: Editable?) {
                text?.run { presenter.passwordTextChanged(this.toString()) }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
    }
}
