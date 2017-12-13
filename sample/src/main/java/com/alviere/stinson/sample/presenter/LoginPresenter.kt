package com.alviere.stinson.sample.presenter

import com.alviere.stinson.Command
import com.alviere.stinson.Idle
import com.alviere.stinson.Message
import com.alviere.stinson.None
import com.alviere.stinson.rx.RxExecutor
import com.alviere.stinson.rx.android.AndroidRxPresenter
import com.alviere.stinson.sample.R
import com.alviere.stinson.sample.state.LoginState
import com.alviere.stinson.sample.view.LoginView
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

class LoginPresenter
    : AndroidRxPresenter<LoginView, LoginState>(AndroidSchedulers.mainThread()) {

    override fun update(message: Message, state: LoginState): Pair<Command, LoginState> {
        return when (message) {
            is LoginTextMessage -> Pair(None(), state.copy(login = message.text))
            is PasswordTextMessage -> Pair(None(), state.copy(password = message.text))
            is LoginButtonClickMessage -> tryLogin(state)
            is LoginResultMessage -> processLogin(message, state)
            else -> Pair(None(), state)
        }
    }

    override fun render(state: LoginState) {
        view?.run {
            setLoginText(state.login)
            setLoginErrorText(state.loginError)
            setLoginErrorEnabled(state.loginErrorEnabled)

            setPasswordText(state.password)
            setPasswordErrorText(state.passwordError)
            setPasswordErrorEnabled(state.passwordErrorEnabled)

            setLoading(state.isLoading)
        }
    }

    override fun executor(command: Command): RxExecutor {
        return when (command) {
            is LoginCommand -> executeLogin(command)
            else -> RxExecutor { Single.just(Idle()) }
        }
    }

    fun loginTextChanged(text: String) {
        stinson.takeIf { text != stinson.state.login }?.accept(LoginTextMessage(text))
    }

    fun passwordTextChanged(text: String) {
        stinson.takeIf { text != stinson.state.password }?.accept(PasswordTextMessage(text))
    }

    fun loginButtonClicked() {
        stinson.accept(LoginButtonClickMessage())
    }

    private fun processLoginText(state: LoginState): LoginState {
        val lengthError = state.login.length < LOGIN_MIN_LENGTH

        return state.copy(
                loginError = if (lengthError) R.string.error_length else R.string.empty_string,
                loginErrorEnabled = lengthError
        )
    }

    private fun processPasswordText(state: LoginState): LoginState {
        val lengthError = state.password.length < PASSWORD_MIN_LENGTH
        val numberError = !state.password.matches(NUMBER_MATCHER)

        return state.copy(
                passwordError = when {
                    lengthError -> R.string.error_length
                    numberError -> R.string.error_number
                    else -> R.string.empty_string
                },
                passwordErrorEnabled = lengthError || numberError
        )
    }

    private fun tryLogin(state: LoginState): Pair<Command, LoginState> {
        val newState = state.run { processPasswordText(processLoginText(this)) }

        return if (newState.loginErrorEnabled || newState.passwordErrorEnabled) {
            Pair(None(), newState)
        } else {
            Pair(LoginCommand(newState.login, newState.password), newState.copy(isLoading = true))
        }
    }

    private fun executeLogin(command: LoginCommand): RxExecutor {
        return RxExecutor {
            Single.create<Message> {
                Thread.sleep(5000L)
                it.onSuccess(LoginResultMessage(Random().nextBoolean()))
            }.subscribeOn(Schedulers.io())
        }
    }

    private fun processLogin(message: LoginResultMessage, state: LoginState): Pair<Command, LoginState> {
        view?.run {
            showSnackbar(if (message.success) R.string.login_success else R.string.login_failure)
        }

        return Pair(None(), state.copy(isLoading = false))
    }

    override fun subscribe(state: LoginState) {} // no-op

    override fun initialState() = LoginState()

    class LoginTextMessage(val text: String) : Message()
    class PasswordTextMessage(val text: String) : Message()
    class LoginButtonClickMessage : Message()
    class LoginResultMessage(val success: Boolean) : Message()

    class LoginCommand(val login: String, val password: String) : Command()

    companion object {
        private const val LOGIN_MIN_LENGTH = 6
        private const val PASSWORD_MIN_LENGTH = 6

        private val NUMBER_MATCHER = Regex(".*\\d+.*")
    }
}