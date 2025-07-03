package com.belajar.catastreamandroidapp.core;

import android.app.Activity
import android.content.Context
import com.auth0.android.Auth0
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials

class Auth0Manager(private val context: Context) {
    private val account = Auth0(
        "TxwwshmdhAjK617oSgqGmzaKboLfqXej",
        "dev-l6v4knjejv1w2y3q.us.auth0.com"
    )

    fun login(activity: Activity, onSuccess: (Credentials) -> Unit, onError: (Throwable) -> Unit) {
        WebAuthProvider.login(account)
            .withScheme("catastream")
            .withScope("openid profile email offline_access")
            .withAudience("https://catastream/api")
            .start(activity, object : com.auth0.android.callback.Callback<Credentials, com.auth0.android.authentication.AuthenticationException> {
                override fun onFailure(exception: com.auth0.android.authentication.AuthenticationException) {
                    onError(exception)
                }
                override fun onSuccess(result: Credentials) {
                    onSuccess(result)
                }
            })
    }

    fun logout(activity: Activity, onComplete: () -> Unit, onError: (Throwable) -> Unit) {
        WebAuthProvider.logout(account)
            .withScheme("catastream")
            .start(activity, object : com.auth0.android.callback.Callback<Void?, com.auth0.android.authentication.AuthenticationException> {
                override fun onFailure(exception: com.auth0.android.authentication.AuthenticationException) {
                    onError(exception)
                }
                override fun onSuccess(result: Void?) {
                    onComplete()
                }
            })
    }
}
