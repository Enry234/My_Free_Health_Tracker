package com.example.myfreehealthtracker

import android.annotation.SuppressLint
import android.content.Intent
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.util.Date


class MainActivity : AppCompatActivity(R.layout.layout_main) {
    lateinit var mainApplication: MainApplication

    private fun loadUser() {

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
                        mainApplication.userDao.getAllUser().collectLatest { user ->
                            user.forEach {
                                Log.i("MAIN", "ciclo caricamento user")

                                mainApplication.userData!!.setUserData(it)
                                if (mainApplication.userData!!.userData.value!!.id != "") {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainApplication = application as MainApplication
        mainApplication.userData = ViewModelProvider(this).get(UserDataViewModel::class.java)
        loadUser()
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    private fun initializeMainActivityLayout() {

        val bottomNavBar = findViewById<BottomNavigationView>(R.id.app_bottom_navigation_bar)
        val toolbar =
            findViewById<androidx.appcompat.widget.Toolbar>(R.id.activity_main_toolbar_root)
        val toolbarTitle = findViewById<TextView>(R.id.activity_main_toolbar_title)
        val drawerLayout = findViewById<DrawerLayout>(R.id.app_drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.app_drawer_navigation_view)
        val toolbarImage = findViewById<ImageView>(R.id.activity_main_toolbar_profile_picture)
        val drawerImage = findViewById<ImageView>(R.id.app_side_drawer_profile_pic)
        val drawerProfileName = findViewById<TextView>(R.id.app_side_drawer_profile_name)
        val a = ImageController()
        val composeViewDrawerHeader = findViewById<ComposeView>(R.id.drawer_header_compose_view)
        drawerImage.setImageURI(a.getImageFromInternalStorage(this, "pictureProfile.png"))
        toolbarImage.setImageURI(a.getImageFromInternalStorage(this, "pictureProfile.png"))
        drawerProfileName.text = "Hello " + mainApplication.userData!!.userData.value!!.nome


        setSupportActionBar(toolbar)
        navigationView.bringToFront()
        val toogle =
            ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.close, R.string.open_drawer)
        drawerLayout.addDrawerListener(toogle)
        toogle.syncState()


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
                    navController.navigate(R.id.sportFragment)
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

        composeViewDrawerHeader.setContent {
            Box(
                modifier = Modifier
                    .height(500.dp)
                    .background(color = Color.White)
                    .padding(10.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box() {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            var peso by remember { mutableStateOf(0.0) }
                            Button(
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    if (peso != 0.0) {
                                        mainApplication.userData!!.userData.value!!.peso!!.add(
                                            Pair(
                                                Date(), peso
                                            )
                                        )
                                        //mainApplication.applicationScope.launch {
                                        runBlocking {
                                            mainApplication.userDao.insertUser(mainApplication.userData!!.userData.value!!)//upsert mode
                                        }
                                        //}
                                        mainApplication.getFirebaseDatabaseRef(FirebaseDBTable.USERS)
                                            .child(mainApplication.userData!!.userData.value!!.id)
                                            .setValue(mainApplication.userData!!)
                                    } else {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Impossibile inserire 0 come peso",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            ) {
                                Text(text = "Aggiorna peso")
                            }
                            TextField(
                                modifier = Modifier.weight(1f),
                                value = peso.toString(), onValueChange = {
                                    try {
                                        peso = it.toDouble()
                                    } catch (ex: Exception) {
                                    }
                                })
                        }
                    }
                    Box() {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            var calorie by remember { mutableStateOf(0) }
                            Button(
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    if (calorie != 0) {
                                        mainApplication.userData!!.userData.value!!.calorie =
                                            calorie
                                        //mainApplication.applicationScope.launch {
                                        runBlocking {
                                            mainApplication.userDao.insertUser(mainApplication.userData!!.userData.value!!)//upsert mode
                                        }
                                        //}
                                        mainApplication.getFirebaseDatabaseRef(FirebaseDBTable.USERS)
                                            .child(mainApplication.userData!!.userData.value!!.id)
                                            .setValue(mainApplication.userData!!)
                                    } else {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Impossibile inserire 0",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            ) {
                                Text(text = "Aggiorna Obiettio Calorie")
                            }
                            TextField(
                                modifier = Modifier.weight(1f),
                                value = calorie.toString(), onValueChange = {
                                    try {
                                        calorie = it.toInt()
                                    } catch (ex: Exception) {
                                    }
                                })
                        }
                    }
                    Box() {
                        Column {
                            Text(text = "Aggiorna obiettivi")
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                                    .weight(1f)
                            ) {
                                //Carbo
                                Column(modifier = Modifier.weight(1f)) {
                                    var carboidrati by remember { mutableIntStateOf(0) }
                                    Button(
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            if (carboidrati != 0) {
                                                mainApplication.userData!!.userData.value!!.carboidrati =
                                                    carboidrati
                                                runBlocking {
                                                    mainApplication.userDao.insertUser(
                                                        mainApplication.userData!!.userData.value!!
                                                    )//upsert mode
                                                }
                                                mainApplication.getFirebaseDatabaseRef(
                                                    FirebaseDBTable.USERS
                                                )
                                                    .child(mainApplication.userData!!.userData.value!!.id)
                                                    .setValue(mainApplication.userData!!)
                                            } else {
                                                Toast.makeText(
                                                    this@MainActivity,
                                                    "Impossibile inserire 0",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    ) {
                                        Text(text = "Aggiorna Obiettivo Carboidrati")
                                    }
                                    TextField(
                                        modifier = Modifier.weight(1f),
                                        value = carboidrati.toString(), onValueChange = {
                                            try {
                                                carboidrati = it.toInt()
                                            } catch (ex: Exception) {
                                            }
                                        })
                                }
                                //proteine
                                Column(modifier = Modifier.weight(1f)) {
                                    var proteine by remember { mutableIntStateOf(0) }
                                    Button(
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            if (proteine != 0) {
                                                mainApplication.userData!!.userData.value!!.proteine =
                                                    proteine
                                                runBlocking {
                                                    mainApplication.userDao.insertUser(
                                                        mainApplication.userData!!.userData.value!!
                                                    )//upsert mode
                                                }
                                                mainApplication.getFirebaseDatabaseRef(
                                                    FirebaseDBTable.USERS
                                                )
                                                    .child(mainApplication.userData!!.userData.value!!.id)
                                                    .setValue(mainApplication.userData!!)
                                            } else {
                                                Toast.makeText(
                                                    this@MainActivity,
                                                    "Impossibile inserire 0",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    ) {
                                        Text(text = "Aggiorna Obiettivo Proteine")
                                    }
                                    TextField(
                                        modifier = Modifier.weight(1f),
                                        value = proteine.toString(), onValueChange = {
                                            try {
                                                proteine = it.toInt()
                                            } catch (ex: Exception) {
                                            }
                                        })
                                }

                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                                    .weight(1f)
                            ) {
                                //Fibre
                                Column(modifier = Modifier.weight(1f)) {
                                    var fibre by remember { mutableIntStateOf(0) }
                                    Button(
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            if (fibre != 0) {
                                                mainApplication.userData!!.userData.value!!.fibre =
                                                    fibre
                                                runBlocking {
                                                    mainApplication.userDao.insertUser(
                                                        mainApplication.userData!!.userData.value!!
                                                    )//upsert mode
                                                }
                                                mainApplication.getFirebaseDatabaseRef(
                                                    FirebaseDBTable.USERS
                                                )
                                                    .child(mainApplication.userData!!.userData.value!!.id)
                                                    .setValue(mainApplication.userData!!)
                                            } else {
                                                Toast.makeText(
                                                    this@MainActivity,
                                                    "Impossibile inserire 0",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    ) {
                                        Text(text = "Aggiorna Obiettivo Fibre")
                                    }
                                    TextField(
                                        modifier = Modifier.weight(1f),
                                        value = fibre.toString(), onValueChange = {
                                            try {
                                                fibre = it.toInt()
                                            } catch (ex: Exception) {
                                            }
                                        })
                                }
                                //grassi
                                Column(modifier = Modifier.weight(1f)) {
                                    var grassi by remember { mutableIntStateOf(0) }
                                    Button(
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            if (grassi != 0) {
                                                mainApplication.userData!!.userData.value!!.grassi =
                                                    grassi
                                                runBlocking {
                                                    mainApplication.userDao.insertUser(
                                                        mainApplication.userData!!.userData.value!!
                                                    )//upsert mode
                                                }
                                                mainApplication.getFirebaseDatabaseRef(
                                                    FirebaseDBTable.USERS
                                                )
                                                    .child(mainApplication.userData!!.userData.value!!.id)
                                                    .setValue(mainApplication.userData!!)
                                            } else {
                                                Toast.makeText(
                                                    this@MainActivity,
                                                    "Impossibile inserire 0",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    ) {
                                        Text(text = "Aggiorna Obiettivo Grassi")
                                    }
                                    TextField(
                                        modifier = Modifier.weight(1f),
                                        value = grassi.toString(), onValueChange = {
                                            try {
                                                grassi = it.toInt()
                                            } catch (ex: Exception) {
                                            }
                                        })
                                }
                            }
                        }

                    }

                }
            }

        }
    }

}



