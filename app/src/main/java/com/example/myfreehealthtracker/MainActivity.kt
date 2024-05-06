package com.example.myfreehealthtracker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices


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
fun getLastKnownLocation(context: Context) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    //permission check
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        //permission success
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    //if location available
                }

            }
        return
    }
}


