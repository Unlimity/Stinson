package com.alviere.stinson.sample.state

import android.annotation.SuppressLint
import com.alviere.stinson.rx.android.ParcelableState
import com.alviere.stinson.sample.R
import kotlinx.android.parcel.Parcelize

@Parcelize
@SuppressLint("ParcelCreator")
data class LoginState(
        val login: String = "",
        val loginError: Int = R.string.empty_string,
        val loginErrorEnabled: Boolean = false,
        val password: String = "",
        val passwordError: Int = R.string.empty_string,
        val passwordErrorEnabled: Boolean = false,
        val isLoading: Boolean = false
) : ParcelableState