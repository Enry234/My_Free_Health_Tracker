package com.example.myfreehealthtracker

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.util.Date


class MainActivity : AppCompatActivity(R.layout.layout_main) {
    private lateinit var mainApplication: MainApplication
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var fileInputStream: FileInputStream
    private lateinit var locationManager: LocationManager
    private fun loadUser() {

        if (mainApplication.internalFileData.exists()) {
            try {
                fileInputStream =
                    applicationContext.openFileInput("internalData")
                val inputStreamReader = InputStreamReader(fileInputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                val stringBuilder = StringBuilder()
                var line: String?
                while (bufferedReader.readLine().also { line = it } != null) {
                    stringBuilder.append(line)
                }

                fileInputStream.close()
                if (stringBuilder.toString() == "UserAccessComplete") {
                    Log.i("MAIN", "LOG file complete")
                    lifecycleScope.launch {
                        Log.i("MAIN", "Coroutine launch")
                        mainApplication.userRepository.allUser.collectLatest {
                            Log.i("MAIN", "ciclo caricamento user")

                            mainApplication.userData!!.setUserData(it)
                            if (mainApplication.userData!!.userData.value!!.id != "") {
                                Log.i("MAIN", mainApplication.userData.toString())
                                firebaseAnalytics = FirebaseAnalytics.getInstance(this@MainActivity)
                                initializeMainActivityLayout()
                            } else {

                                Log.i("MAIN_ERROR", "Internal user db not found")


                            }

                        }

                    }

                }

            } catch (e: IOException) {
                e.printStackTrace()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)


            }
        } else {
            Log.i("MAIN_ERROR", "Internal file not found")

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainApplication = application as MainApplication
        mainApplication.userData = ViewModelProvider(this)[UserDataViewModel::class.java]
        loadUser()

    }
    override fun onDestroy() {
        super.onDestroy()


        Log.i("MainActivity", "onDestroy called")

        // Cancel any  coroutines
        lifecycleScope.coroutineContext.cancel()

        // Close file streams if any are open
        try {
            fileInputStream.close()
        } catch (e: IOException) {
        }

    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    private fun initializeMainActivityLayout() {

        val bottomNavBar = findViewById<BottomNavigationView>(R.id.app_bottom_navigation_bar)
        val toolbar =
            findViewById<androidx.appcompat.widget.Toolbar>(R.id.activity_main_toolbar_root)
        val toolbarTitle = findViewById<TextView>(R.id.activity_main_toolbar_title)
        val toolbarImage = findViewById<ImageView>(R.id.activity_main_toolbar_profile_picture)
        val a = ImageController()
        toolbarImage.setImageURI(a.getImageFromInternalStorage(this, "pictureProfile.png"))


        val fragmentContainer = findViewById<FragmentContainerView>(
            R.id.activity_main_fragment_container
        )

        //bottomNavBar
        bottomNavBar.setOnItemSelectedListener {
            val navController = fragmentContainer.findNavController()
            when (it.itemId) {
                R.id.Home -> {
                    toolbarTitle.text = getString(R.string.homeText)
                    navController.navigate(R.id.homeFragment)
                    true
                }

                R.id.Sport -> {
                    toolbarTitle.text = getString(R.string.sportText)
                    navController.navigate(R.id.sportFragment)
                    true
                }


                R.id.Health -> {
                    toolbarTitle.text = getString(R.string.healthText)
                    navController.navigate(R.id.healthFragment)
                    true
                }

                else -> false
            }
        }
        location()

    }

    private fun location() {
        try {
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            if (ActivityCompat.checkSelfPermission(
                    this,
                    ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    2
                )
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    2
                )
            } else {

                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            val strLocation =
                                location.latitude.toString() + "|" + location.longitude.toString()
                            mainApplication.userData!!.userData.value!!.posizione = strLocation

                            mainApplication.getFirebaseDatabaseRef(FirebaseDBTable.USERS)
                                .child(mainApplication.userData!!.userData.value!!.id)
                                .setValue(mainApplication.userData!!.userData.value!!)
                            val bundle = Bundle().apply {
                                putString(
                                    "position",
                                    mainApplication.userData!!.userData.value!!.posizione
                                )
                            }
                            firebaseAnalytics.logEvent("hasUpdatePosition", bundle)
                        }
                    }

            }
        } catch (ex: Exception) {
            Log.i("location", "ErrorLoadingPosition")
        }


    }
}



