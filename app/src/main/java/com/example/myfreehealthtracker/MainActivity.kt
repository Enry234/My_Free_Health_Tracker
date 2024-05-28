package com.example.myfreehealthtracker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader


class MainActivity : AppCompatActivity(R.layout.layout_main) {
    lateinit var mainApplication: MainApplication
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainApplication = application as MainApplication
        if (mainApplication.internalFileData.exists()) {
            try {
                val fileInputStream: FileInputStream =
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
                        mainApplication.userDao.getUser().collectLatest { user ->
                            user.forEach {
                                Log.i("MAIN", "ciclo caricamento user")
                                mainApplication.userData = it
                                if (mainApplication.userData!!.id != "") {
                                    Log.i("MAIN", mainApplication.userData.toString())
                                    initializeMainActivityLayout()
                                } else {

                                    Log.i("MAIN_ERROR", "Internal user db not found")

                                }
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

    private fun initializeMainActivityLayout() {

        val bottomNavBar = findViewById<BottomNavigationView>(
            R.id.app_bottom_navigation_bar
        )
        val toolbarTitle = findViewById<TextView>(R.id.activity_main_toolbar_title)
        val drawerLayout = findViewById<DrawerLayout>(R.id.app_drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.app_drawer_navigation_view)
        val toolbarImage = findViewById<ImageView>(R.id.activity_main_toolbar_profile_picture)
        var a = ImageController()

        toolbarImage.setImageURI(a.getImageFromInternalStorage(this, "pictureProfile.png"))
        val fragmentContainer = findViewById<FragmentContainerView>(
            R.id.activity_main_fragment_container
        )

        findViewById<ImageView>(R.id.activity_main_toolbar_profile_picture)
            .setOnClickListener {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        bottomNavBar.setOnItemSelectedListener {
            val navController = fragmentContainer.findNavController()
            when (it.itemId) {
                R.id.Home -> {
                    toolbarTitle.text = getString(R.string.Home)
                    navController.navigate(R.id.homeFragment)
                    true
                }

                R.id.Sport -> {
                    toolbarTitle.text = getString(R.string.Sport)
                    navController.navigate(R.id.barcodeFragment)
                    true
                }


                R.id.Health -> {
                    toolbarTitle.text = getString(R.string.Health)
                    navController.navigate(R.id.healthFragment)
                    true
                }

                else -> false
            }
        }

        navigationView.setNavigationItemSelectedListener {
            val navController = fragmentContainer.findNavController()
            when (it.itemId) {
                R.id.Logout -> {
                    Log.i("Logout", "Entrato condizioner")
                    navController.navigate(R.id.sportFragment)
                    true
                }

                else -> false
            }
        }

    }
}

