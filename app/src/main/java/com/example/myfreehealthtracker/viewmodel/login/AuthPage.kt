package com.example.myfreehealthtracker.viewmodel.login

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import com.example.myfreehealthtracker.FirebaseDBTable
import com.example.myfreehealthtracker.MainApplication
import com.example.myfreehealthtracker.R
import com.example.myfreehealthtracker.localdatabase.Entities.UserData
import com.example.myfreehealthtracker.viewmodel.app.MainActivity
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.auth
import com.google.firebase.storage.FirebaseStorage
import com.vsnappy1.datepicker.DatePicker
import com.vsnappy1.datepicker.data.model.DatePickerDate
import com.vsnappy1.datepicker.data.model.SelectionLimiter
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.FileOutputStream
import java.io.IOException
import java.util.Calendar
import java.util.Date

class AuthPage {

    enum class LoginScreen {
        FIRST_LAST_NAME, BIRTHDATE_GENDER, HEIGHT_WEIGHT, IMAGE, DONE
    }

    class UserState {
        var firstName by mutableStateOf("")
        var lastName by mutableStateOf("")
        var birthdate by mutableStateOf("Seleziona la data di nascita")
        var gender by mutableStateOf("")
        var height by mutableStateOf("")
        var weight by mutableStateOf("")
        var imageUri by mutableStateOf<Uri?>(null)

        fun isValidInput(screen: LoginScreen): Boolean {
            return when (screen) {
                LoginScreen.FIRST_LAST_NAME -> {
                    return firstName.trim().isNotEmpty() && lastName.trim().isNotEmpty()
                }

                LoginScreen.BIRTHDATE_GENDER -> {
                    !(birthdate == "Seleziona la data di nascita" || gender.isEmpty())
                }

                LoginScreen.HEIGHT_WEIGHT -> {
                    val height = height.trim().toFloatOrNull()
                    val weight = weight.trim().toFloatOrNull()

                    height != null && weight != null
                }

                LoginScreen.IMAGE -> true
                LoginScreen.DONE -> true
            }
        }

        fun getUserData(): UserData {
            val user = UserData(
                nome = firstName,
                cognome = lastName,
                dataDiNascita = birthdate,
                sesso = gender,
                altezza = height.toFloat().toInt(),
                peso = mutableListOf(),
                image = imageUri.toString()
            )
            user.peso?.add(Date() to weight.toFloat().toDouble())
            return user
        }

        // DEBUG
        override fun toString(): String {
            return "UserState(firstName='$firstName', lastName='$lastName', birthdate='$birthdate', gender='$gender', height='$height', weight='$weight', imageUri=$imageUri)"
        }
    }

    private lateinit var user: UserData
    private val userState = UserState()

    private var currentScreen: LoginScreen by mutableStateOf(LoginScreen.FIRST_LAST_NAME)
    private var nameError by mutableStateOf(false)
    private var weightHeightError by mutableStateOf(false)


    @Composable
    fun LoginForm() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            Surface(
                modifier = Modifier.padding(top = 32.dp),
                tonalElevation = 16.dp,
                shadowElevation = 16.dp,
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        when (currentScreen) {
                            LoginScreen.FIRST_LAST_NAME -> FirstLastNameForm()
                            LoginScreen.BIRTHDATE_GENDER -> BirthdateGenderForm()
                            LoginScreen.HEIGHT_WEIGHT -> HeightWeightForm()
                            LoginScreen.IMAGE -> ImageForm()
                            LoginScreen.DONE -> {
                                RegistrationComplete()
                                Log.i("MYDEBUG", userState.toString())

                                // FILL user from userState
                                user = userState.getUserData()
                                // PUSH DATA
                                onLoginSuccess(
                                    context = LocalContext.current,
                                    lifecycleOwner = LocalLifecycleOwner.current
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun RegistrationComplete() {
        Text(
            text = stringResource(id = R.string.registrationComplete),
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
        )
    }

    @Composable
    private fun LoginHeader() {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(top = 32.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.mipmap.unhealth),
                    contentDescription = "Unhelath",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth(0.7f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(id = R.string.welcome),
                    fontSize = 40.sp,
                )
                Text(text = stringResource(id = R.string.insertDataCompleteAccess), textAlign = TextAlign.Center)
            }
        }
    }

    @Composable
    private fun FirstLastNameForm() {
        LoginHeader()
        OutlinedTextField(
            isError = nameError,
            value = userState.firstName,
            onValueChange = { userState.firstName = it },
            label = { Text(stringResource(id = R.string.insertName)) },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    Icons.Filled.AccountCircle,
                    contentDescription = stringResource(id = R.string.insertName)
                )
            },
            singleLine = true
        )
        OutlinedTextField(
            isError = nameError,
            value = userState.lastName,
            onValueChange = { userState.lastName = it },
            label = { Text(stringResource(id = R.string.insertCognome)) },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    Icons.Filled.AccountCircle,
                    contentDescription = stringResource(id = R.string.insertCognome)
                )
            },
            singleLine = true
        )
        ActionBar(LoginScreen.FIRST_LAST_NAME, null, LoginScreen.BIRTHDATE_GENDER)
    }

    @Composable
    private fun ActionBar(current: LoginScreen, back: LoginScreen?, next: LoginScreen?) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            if (back != null) BackButton(back)
            if (next != null) {
                Spacer(Modifier.width(0.dp))
                NextButton(current, next)
            }
        }
    }

    @Composable
    private fun BirthdateGenderForm() {
        LoginHeader()
        var isDatePickerOpened by remember { mutableStateOf(false) }
        // BIRTHDATE
        Button(
            onClick = {
                isDatePickerOpened = !isDatePickerOpened
            }, modifier = Modifier.height(50.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = userState.birthdate)
                Icon(Icons.Filled.DateRange, contentDescription = "Date Selection")
            }
        }

        if (isDatePickerOpened) {
            var year by remember { mutableIntStateOf(0) }
            var month by remember { mutableIntStateOf(0) }
            var day by remember { mutableIntStateOf(0) }

            val calendar = Calendar.getInstance()

            AlertDialog(onDismissRequest = { isDatePickerOpened = false }, confirmButton = {
                Button(modifier = Modifier.fillMaxWidth(), onClick = {
                    isDatePickerOpened = false
                }) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            }, text = {
                DatePicker(
                    onDateSelected = { y, m, d ->
                        year = y
                        month = m
                        day = d
                        userState.birthdate = "$day/${month + 1}/$year"
                    }, selectionLimiter = SelectionLimiter(
                        toDate = DatePickerDate(
                            year = calendar.get(Calendar.YEAR),
                            month = calendar.get(Calendar.MONTH),
                            day = calendar.get(Calendar.DAY_OF_MONTH)
                        )
                    )
                )
            })

        }
        // GENDER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = userState.gender == "M", onClick = {
                    userState.gender = "M"
                })
                Text(text = "Maschio")
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Femmina")
                RadioButton(selected = userState.gender == "F", onClick = {
                    userState.gender = "F"
                })
            }
        }
        ActionBar(
            LoginScreen.BIRTHDATE_GENDER, LoginScreen.FIRST_LAST_NAME, LoginScreen.HEIGHT_WEIGHT
        )
    }

    @Composable
    private fun HeightWeightForm() {
        LoginHeader()
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            OutlinedTextField(
                isError = weightHeightError,
                modifier = Modifier.weight(1f),
                value = userState.height,
                onValueChange = { userState.height = it },
                label = { Text(stringResource(id = R.string.height)) },
                suffix = { Text(stringResource(id = R.string.cm)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedTextField(
                isError = weightHeightError,
                modifier = Modifier.weight(1f),
                value = userState.weight,
                onValueChange = { userState.weight = it },
                label = { Text(stringResource(id = R.string.weight)) },
                suffix = { Text(text = stringResource(id = R.string.kg)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
        ActionBar(
            LoginScreen.HEIGHT_WEIGHT, LoginScreen.BIRTHDATE_GENDER, LoginScreen.IMAGE
        )
    }

    @Composable
    private fun ImageForm() {
        val context = LocalContext.current
        LoginHeader()
        Box(
            modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
        ) {

            Text(
                text = stringResource(id = R.string.selectPhotoOr), textAlign = TextAlign.Center
            )
        }
        var isPhotoInserted by remember { mutableStateOf(false) }
        var isPhotoSelectorOpened by remember { mutableStateOf(false) }

        val photoSelectorLauncher =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia(),
                onResult = { uri ->
                    userState.imageUri = uri
                    userState.imageUri?.let {
                        Log.i("login", "uri success")
                        val imageController = ImageController()
                        if (imageController.saveImageToInternalStorage(
                                context, it, "pictureProfile.png"
                            )
                        ) Log.i("login", "image success")
                        else Log.i("login", "image error")
                        if (uri != null) {


                        }
                    }
                    isPhotoInserted = true
                })

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
            ) {
                AsyncImage(
                    model = userState.imageUri,
                    contentDescription = stringResource(id = R.string.selectPhoto),
                    modifier = Modifier
                        .width(200.dp)
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(modifier = Modifier.width(200.dp), onClick = { isPhotoSelectorOpened = true }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = stringResource(id = R.string.selectPhoto))
                    Icon(
                        imageVector = Icons.Outlined.AccountBox,
                        contentDescription = stringResource(id = R.string.selectPhoto)
                    )
                }
            }
        }

        if (isPhotoSelectorOpened) {
            photoSelectorLauncher.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
            isPhotoSelectorOpened = false
        }
        Text(
            text = stringResource(id = R.string.allertInsertImage), textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        ActionBar(
            LoginScreen.IMAGE, LoginScreen.HEIGHT_WEIGHT, LoginScreen.DONE
        )
    }

    @Composable
    private fun NextButton(current: LoginScreen, next: LoginScreen) {
        val context = LocalContext.current
        Button(modifier = Modifier, onClick = {
            if (userState.isValidInput(current)) {
                currentScreen = next
            } else {
                when (currentScreen) {
                    LoginScreen.FIRST_LAST_NAME -> nameError = true
                    LoginScreen.HEIGHT_WEIGHT -> weightHeightError = true
                    else -> {}
                }
                Toast.makeText(
                    context, context.getString(R.string.errorInsertData), Toast.LENGTH_SHORT
                ).show()
            }
        }) {
            Text(stringResource(id = R.string.foward))
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = stringResource(id = R.string.foward)
            )
        }
    }

    @Composable
    private fun BackButton(back: LoginScreen) {
        Button(modifier = Modifier, onClick = { currentScreen = back }) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(id = R.string.back)
            )
            Text(stringResource(id = R.string.back))
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun onLoginSuccess(context: Context, lifecycleOwner: LifecycleOwner) {
        val mainApplication = context.applicationContext as MainApplication
        mainApplication.userData!!.setUserData(user)
        val firebaseRef = mainApplication.getFirebaseDatabaseRef(FirebaseDBTable.USERS)
        user.id = firebaseRef.push().toString()
            .removePrefix("https://my-free-health-tracker-default-rtdb.europe-west1.firebasedatabase.app/users/")
        firebaseRef.child(user.id).setValue(user)
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        lifecycleOwner.lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                mainApplication.userRepository.insertUser(user)
            }
            if (result != -1L) {
                Log.i("loginpage", "insert success")
                try {
                    val fileOutputStream: FileOutputStream = context.openFileOutput(
                        "internalData", Context.MODE_PRIVATE
                    )
                    fileOutputStream.write("UserAccessComplete".toByteArray())
                    fileOutputStream.close()


                    // Log a custom event

                    GlobalScope.launch {
                        try {


                            val imageController = ImageController()
                            Log.i("loginpage", "Launch upload Image Firebase")
                            uploadImage(
                                imageController.getImageFromInternalStorage(
                                    context, "pictureProfile.png"
                                )!!, user.id
                            )
                        } catch (e: Exception) {

                        }
                    }

                    val bundle = Bundle().apply {
                        putString("id", user.id)
                    }
                    firebaseAnalytics.logEvent("hasCompleteRegistration", bundle)
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                    (context as? ComponentActivity)?.finish()
                } catch (e: IOException) {
                    Log.i("loginpage", "error write file")
                    e.printStackTrace()
                }
            } else {
                Log.i("loginpage", "error insert")
            }

        }
    }

    private suspend fun uploadImage(uri: Uri, id: String) {

        val auth = Firebase.auth
        try {
            //get User
            val userCredential = auth.signInAnonymously().await()
            val user = userCredential.user //dummy
            Log.i("Firebase", "anonymous user success")

            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference
            val imageRef = storageRef.child("images/${id}/${uri.lastPathSegment}")
            Log.i("Firebase", "imageRef sucess")
            imageRef.putFile(uri).addOnSuccessListener { _ ->
                Log.e("Firebase", "Image Profile upload into Firebase Storage ")
            }.addOnFailureListener { _ ->
                Log.e("Firebase", "Image Profile upload Error into Firebase Storage ")
            }
        } catch (e: Exception) {
            Log.i("Firebase", "Image Profile catch error ", e)
        }

    }
}