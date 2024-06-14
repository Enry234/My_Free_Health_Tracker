package com.example.myfreehealthtracker.viewmodel.login

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.example.myfreehealthtracker.ApplicationTheme

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val a = AuthPage()
        setContent {
            ApplicationTheme {
                a.LoginForm()
            }
        }
    }
}