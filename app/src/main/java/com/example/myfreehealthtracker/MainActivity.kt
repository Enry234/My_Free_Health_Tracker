package com.example.myfreehealthtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent


class MainActivity : ComponentActivity() {
    private lateinit var user: UserData
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        val a: LoginPage = LoginPage()
        setContent {

            a.Login()
        }


    }
}


//return the fine last location
