package com.belajar.catastreamandroidapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.belajar.catastreamandroidapp.core.Auth0Manager
import com.belajar.catastreamandroidapp.feature.ui.navigation.MainScreen
import com.belajar.catastreamandroidapp.feature.user.ui.LoginScreen

class MainActivity : ComponentActivity() {
    private lateinit var auth0Manager: Auth0Manager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth0Manager = Auth0Manager(this)

        setContent {
            var isLoggedIn by remember { mutableStateOf(false) }
            var errorMessage by remember { mutableStateOf<String?>(null) }
            var authToken by remember { mutableStateOf<String?>(null) }

            if (!isLoggedIn) {
                LoginScreen(
                    onLoginClick = {
                        auth0Manager.login(
                            activity = this@MainActivity,
                            onSuccess = { credentials ->
                                isLoggedIn = true
                                authToken = credentials.accessToken
                            },
                            onError = { error ->
                                errorMessage = error.message
                            }
                        )
                    },
                    errorMessage = errorMessage
                )
            } else {
                authToken?.let { token ->
                    MainScreen(
                        token = token,
                        onLogout = {
                            auth0Manager.logout(
                                activity = this@MainActivity,
                                onComplete = {
                                    isLoggedIn = false
                                    authToken = null
                                },
                                onError = { error -> errorMessage = error.message }
                            )
                        }
                    )
                }
            }
        }
    }
}
