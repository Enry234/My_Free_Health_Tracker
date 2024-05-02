package com.example.myfreehealthtracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


class LoginPage {

    @Preview
    @Composable
    fun Login() {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier
                .padding(5.dp)
                .fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                        .fillMaxHeight(0.2f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Inserisci i tuoi dati per completare l'accesso",
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Benvenuto",
                        textAlign = TextAlign.Center,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}