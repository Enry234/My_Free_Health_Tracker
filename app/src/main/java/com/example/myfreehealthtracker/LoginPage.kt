package com.example.myfreehealthtracker

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults.DecorationBox
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.myfreehealthtracker.Models.UserData
import com.google.android.gms.location.LocationServices
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects

@OptIn(ExperimentalMaterial3Api::class)
class LoginPage {
    private var user: UserData = UserData()


    @SuppressLint("UnrememberedMutableState", "SimpleDateFormat")
    @Preview
    @Composable
    fun Login() {
        var pos by remember {
            mutableIntStateOf(4)
        }



        Surface(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxSize()
            ) {
                Column(modifier = Modifier.fillMaxSize()) {


                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .fillMaxHeight(0.2f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Benvenuto",
                            textAlign = TextAlign.Center,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Inserisci i tuoi dati per completare l'accesso",
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.6f)
                            .background(color = Color.LightGray)
                            .padding(10.dp)
                            .align(alignment = Alignment.CenterHorizontally)

                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxSize()
                        ) {

                            when (pos) {
                                0 -> {
                                    var error by remember {
                                        mutableStateOf(false)
                                    }
                                    var nome by remember {
                                        mutableStateOf("")
                                    }
                                    var cognome by remember {
                                        mutableStateOf("")
                                    }
                                    val interactionSource = remember { MutableInteractionSource() }

                                    BasicTextField(
                                        value = nome,
                                        onValueChange = { nome = it },
                                        // internal implementation of the BasicTextField will dispatch focus events
                                        interactionSource = interactionSource,
                                        enabled = true,
                                        singleLine = true,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight(0.6f)
                                    ) {
                                        DecorationBox(
                                            value = nome,
                                            label = { Text(text = "Inserisci i tuoi dati") },
                                            innerTextField = {
                                                Column(
                                                    modifier = Modifier.fillMaxSize(),
                                                    verticalArrangement = Arrangement.Center,
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {

                                                    TextField(
                                                        value = nome,
                                                        onValueChange = { nome = it },
                                                        placeholder = {
                                                            Text(
                                                                text = "Nome"
                                                            )
                                                        },
                                                        label = {
                                                            Text(
                                                                text = "Inserisci il tuo Nome",
                                                                fontSize = 12.sp
                                                            )
                                                        },
                                                        singleLine = true,
                                                        modifier = Modifier.fillMaxWidth(0.8f),
                                                        colors = if (error) {
                                                            TextFieldDefaults.colors(
                                                                unfocusedContainerColor = Color.Red.copy(
                                                                    0.2f
                                                                ),
                                                                focusedContainerColor = Color.Red.copy(
                                                                    0.2f
                                                                )
                                                            )
                                                        } else {
                                                            TextFieldDefaults.colors()
                                                        }


                                                    )
                                                    TextField(
                                                        value = cognome,
                                                        onValueChange = { cognome = it },
                                                        placeholder = {
                                                            Text(
                                                                text = "Cognome"
                                                            )
                                                        },
                                                        label = {
                                                            Text(
                                                                text = "Inserisci il tuo Cognome",
                                                                fontSize = 12.sp
                                                            )
                                                        },
                                                        singleLine = true,
                                                        modifier = Modifier.fillMaxWidth(0.8f),
                                                        colors = if (error) {
                                                            TextFieldDefaults.colors(
                                                                unfocusedContainerColor = Color.Red.copy(
                                                                    0.2f
                                                                ),
                                                                focusedContainerColor = Color.Red.copy(
                                                                    0.2f
                                                                )
                                                            )
                                                        } else {
                                                            TextFieldDefaults.colors()
                                                        }


                                                    )
                                                }
                                            },
                                            enabled = true,
                                            singleLine = false,
                                            visualTransformation = VisualTransformation.None,
                                            interactionSource = interactionSource,
                                            colors = OutlinedTextFieldDefaults.colors(),
                                            isError = error,
                                            contentPadding = OutlinedTextFieldDefaults.contentPadding(),
                                            container = {
                                                TextFieldDefaults.ContainerBox(
                                                    true,
                                                    error,
                                                    interactionSource,
                                                    OutlinedTextFieldDefaults.colors()
                                                )

                                            },
                                            trailingIcon = {
                                                IconButton(onClick = {
                                                    if (nome.isNotEmpty() && nome.isNotBlank() && cognome.isNotEmpty() && cognome.isNotBlank()) {
                                                        user.nome = nome
                                                        user.cognome = cognome
                                                        pos += 1
                                                    } else {
                                                        error = true
                                                    }
                                                }) {
                                                    Icon(
                                                        imageVector = Icons.Filled.ArrowForward,
                                                        contentDescription = ""
                                                    )
                                                }
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Outlined.AccountCircle,
                                                    contentDescription = ""
                                                )
                                            }
                                        )
                                    }
                                }

                                1 -> {
                                    var error by remember { mutableStateOf(false) }
                                    var dateResult by remember {
                                        mutableStateOf(Date())
                                    }
                                    val openDialog = remember {
                                        mutableStateOf(false)
                                    }
                                    var inserted by remember { mutableStateOf(false) }
                                    val datePickerState = rememberDatePickerState()
                                    val interactionSource = remember { MutableInteractionSource() }
                                    var empty by remember {
                                        mutableStateOf("")
                                    }
                                    var dateText by remember {
                                        mutableStateOf("Clicca per inserire la data di nascita")
                                    }
                                    BasicTextField(
                                        value = empty,
                                        onValueChange = { empty = it },
                                        // internal implementation of the BasicTextField will dispatch focus events
                                        interactionSource = interactionSource,
                                        enabled = true,
                                        singleLine = true,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight(0.6f)
                                    ) {
                                        DecorationBox(
                                            value = empty,
                                            label = { Text(text = "Inserisci i tuoi dati") },
                                            innerTextField = {
                                                Column(
                                                    modifier = Modifier.fillMaxSize(),
                                                    verticalArrangement = Arrangement.Center,
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    OutlinedButton(
                                                        onClick = { openDialog.value = true },
                                                        modifier = Modifier.fillMaxWidth(0.8f),
                                                        colors = if (error) {
                                                            ButtonDefaults.buttonColors(
                                                                containerColor = Color.Red.copy(0.2f)
                                                            )
                                                        } else {
                                                            ButtonDefaults.buttonColors(
                                                                containerColor = Color.Blue.copy(
                                                                    0.2f
                                                                )
                                                            )
                                                        }
                                                    ) {
                                                        Text(
                                                            text = dateText,
                                                            textAlign = TextAlign.Center,
                                                            minLines = 2,
                                                            maxLines = 2
                                                        )
                                                    }

                                                    if (openDialog.value) {

                                                        val confirmEnable =
                                                            derivedStateOf { datePickerState.selectedDateMillis != null }
                                                        DatePickerDialog(onDismissRequest = {
                                                            openDialog.value = false
                                                        }, confirmButton = {
                                                            TextButton(onClick = {
                                                                openDialog.value = false
                                                                var date = Date()
                                                                if (datePickerState.selectedDateMillis != null) {
                                                                    date =
                                                                        Date(datePickerState.selectedDateMillis!!)
                                                                }
                                                                dateResult = date
                                                                inserted = true
                                                                val format =
                                                                    SimpleDateFormat("dd/MM/yyy")
                                                                dateText = format.format(date)
                                                            }, enabled = confirmEnable.value) {
                                                                Text(text = "Fatto")
                                                            }
                                                        }) {
                                                            DatePicker(state = datePickerState)
                                                        }
                                                    }
                                                }

                                            },
                                            enabled = true,
                                            singleLine = false,
                                            visualTransformation = VisualTransformation.None,
                                            interactionSource = interactionSource,
                                            colors = OutlinedTextFieldDefaults.colors(),
                                            isError = error,
                                            contentPadding = OutlinedTextFieldDefaults.contentPadding(),
                                            container = {
                                                TextFieldDefaults.ContainerBox(
                                                    true,
                                                    error,
                                                    interactionSource,
                                                    OutlinedTextFieldDefaults.colors()
                                                )

                                            },
                                            trailingIcon = {
                                                IconButton(onClick = {
                                                    if (inserted) {
                                                        if (dateResult < Date()) {
                                                            user.dataDiNascita = dateResult
                                                            pos += 1
                                                        } else {
                                                            error = true
                                                        }
                                                    } else {
                                                        error = true
                                                    }
                                                }) {
                                                    Icon(
                                                        imageVector = Icons.Filled.ArrowForward,
                                                        contentDescription = ""
                                                    )
                                                }
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Outlined.DateRange,
                                                    contentDescription = ""
                                                )
                                            }
                                        )
                                    }
                                }

                                2 -> {
                                    var error by remember { mutableStateOf(false) }
                                    val interactionSource = remember { MutableInteractionSource() }
                                    var sesso by remember {
                                        mutableStateOf(" ")
                                    }
                                    BasicTextField(
                                        value = sesso,
                                        onValueChange = { sesso = it },
                                        // internal implementation of the BasicTextField will dispatch focus events
                                        interactionSource = interactionSource,
                                        enabled = true,
                                        singleLine = true,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight(0.6f)
                                    ) {
                                        DecorationBox(
                                            value = sesso,
                                            label = { Text(text = "Inserisci i tuoi dati") },
                                            innerTextField = {
                                                Column(
                                                    modifier = Modifier.fillMaxSize(),
                                                    verticalArrangement = Arrangement.Center,
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    val radioOptions = listOf("M", "F")
                                                    val (selectedOption, onOptionSelected) = remember {
                                                        mutableStateOf(
                                                            radioOptions[0]
                                                        )
                                                    }
                                                    sesso = "M"

                                                    Row(
                                                        horizontalArrangement = Arrangement.SpaceEvenly,
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        RadioButton(
                                                            selected = ("M" == selectedOption),
                                                            onClick = {
                                                                onOptionSelected("M"); sesso = "M"
                                                            })

                                                        RadioButton(
                                                            selected = ("F" == selectedOption),
                                                            onClick = {
                                                                onOptionSelected("F"); sesso = "F"
                                                            })

                                                    }
                                                    Row(
                                                        horizontalArrangement = Arrangement.SpaceEvenly,
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Text(
                                                            text = "Maschio",
                                                            textAlign = TextAlign.Center
                                                        )

                                                        Text(
                                                            text = "Femmina",
                                                            textAlign = TextAlign.Center
                                                        )

                                                    }
                                                }
                                            },
                                            enabled = true,
                                            singleLine = false,
                                            visualTransformation = VisualTransformation.None,
                                            interactionSource = interactionSource,
                                            colors = OutlinedTextFieldDefaults.colors(),
                                            isError = error,
                                            contentPadding = OutlinedTextFieldDefaults.contentPadding(),
                                            container = {
                                                TextFieldDefaults.ContainerBox(
                                                    true,
                                                    error,
                                                    interactionSource,
                                                    OutlinedTextFieldDefaults.colors()
                                                )

                                            },
                                            trailingIcon = {
                                                IconButton(onClick = {
                                                    if (sesso != " ") {
                                                        user.sesso = sesso[0]
                                                        pos += 1
                                                    } else {
                                                        error = true
                                                    }
                                                }) {
                                                    Icon(
                                                        imageVector = Icons.Filled.ArrowForward,
                                                        contentDescription = ""
                                                    )
                                                }
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Outlined.Face,
                                                    contentDescription = ""
                                                )
                                            }
                                        )
                                    }
                                }

                                3 -> {
                                    var error by remember {
                                        mutableStateOf(false)
                                    }
                                    var peso by remember {
                                        mutableStateOf(0)
                                    }
                                    var altezza by remember {
                                        mutableStateOf(0)
                                    }
                                    val interactionSource = remember { MutableInteractionSource() }

                                    BasicTextField(
                                        value = peso.toString(),
                                        onValueChange = { peso = it.toInt() },
                                        // internal implementation of the BasicTextField will dispatch focus events
                                        interactionSource = interactionSource,
                                        enabled = true,
                                        singleLine = true,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight(0.6f)
                                    ) {
                                        DecorationBox(
                                            value = peso.toString(),
                                            label = { Text(text = "Inserisci i tuoi dati") },
                                            innerTextField = {
                                                Column(
                                                    modifier = Modifier.fillMaxSize(),
                                                    verticalArrangement = Arrangement.Center,
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {

                                                    TextField(
                                                        value = peso.toString(),
                                                        onValueChange = {
                                                            try {
                                                                peso = it.toInt()
                                                            } catch (ex: NumberFormatException) {
                                                            }
                                                        },
                                                        placeholder = {
                                                            Text(
                                                                text = "Peso"
                                                            )
                                                        },
                                                        label = { Text(text = "Inserisci il tuo Peso") },
                                                        suffix = { Text(text = "Kg") },
                                                        singleLine = true,
                                                        modifier = Modifier.fillMaxWidth(0.8f),
                                                        colors = if (error) {
                                                            TextFieldDefaults.colors(
                                                                unfocusedContainerColor = Color.Red.copy(
                                                                    0.2f
                                                                ),
                                                                focusedContainerColor = Color.Red.copy(
                                                                    0.2f
                                                                )
                                                            )
                                                        } else {
                                                            TextFieldDefaults.colors()
                                                        },
                                                        keyboardOptions = KeyboardOptions(
                                                            keyboardType = KeyboardType.Number,
                                                            imeAction = ImeAction.Done
                                                        )


                                                    )
                                                    TextField(
                                                        value = altezza.toString(),
                                                        onValueChange = {
                                                            try {
                                                                altezza = it.toInt()
                                                            } catch (ex: NumberFormatException) {
                                                            }
                                                        },
                                                        placeholder = {
                                                            Text(
                                                                text = "Altezza"
                                                            )
                                                        },
                                                        suffix = { Text(text = "Cm") },
                                                        label = { Text(text = "Inserisci la tua Altezza") },
                                                        singleLine = true,
                                                        modifier = Modifier.fillMaxWidth(0.8f),
                                                        colors = if (error) {
                                                            TextFieldDefaults.colors(
                                                                unfocusedContainerColor = Color.Red.copy(
                                                                    0.2f
                                                                ),
                                                                focusedContainerColor = Color.Red.copy(
                                                                    0.2f
                                                                )
                                                            )
                                                        } else {
                                                            TextFieldDefaults.colors()
                                                        },
                                                        keyboardOptions = KeyboardOptions(
                                                            keyboardType = KeyboardType.Number,
                                                            imeAction = ImeAction.Done
                                                        )

                                                    )
                                                }
                                            },
                                            enabled = true,
                                            singleLine = false,
                                            visualTransformation = VisualTransformation.None,
                                            interactionSource = interactionSource,
                                            colors = OutlinedTextFieldDefaults.colors(),
                                            isError = error,
                                            contentPadding = OutlinedTextFieldDefaults.contentPadding(),
                                            container = {
                                                TextFieldDefaults.ContainerBox(
                                                    true,
                                                    error,
                                                    interactionSource,
                                                    OutlinedTextFieldDefaults.colors()
                                                )

                                            },
                                            trailingIcon = {
                                                IconButton(onClick = {
                                                    if (altezza > 0 && peso > 0) {
                                                        user.altezza = altezza
                                                        user.peso =
                                                            mutableListOf(Pair(Date(), peso))
                                                        pos += 1
                                                    } else {
                                                        error = true
                                                    }
                                                }) {
                                                    Icon(
                                                        imageVector = Icons.Filled.ArrowForward,
                                                        contentDescription = ""
                                                    )
                                                }
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Outlined.Build,
                                                    contentDescription = ""
                                                )
                                            }
                                        )
                                    }


                                }

                                4 -> {
                                    val interactionSource = remember { MutableInteractionSource() }

                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Registrazione completata",
                                            fontSize = 34.sp,
                                            fontWeight = FontWeight.Black,
                                            textAlign = TextAlign.Center
                                        )
                                        OutlinedButton(
                                            onClick = {

                                            }
                                        ) {
                                            Text(text = "Clicca qui per Inserire una tua Foto")
                                        }
                                        Spacer(modifier = Modifier.padding(10.dp))
                                        Text(text = "Oppure")
                                        Spacer(modifier = Modifier.padding(10.dp))
                                        var apri by remember {
                                            mutableStateOf(false)
                                        }
                                        OutlinedButton(
                                            onClick = {
                                                apri = true
                                            },
                                            modifier = Modifier.hoverable(interactionSource = interactionSource)
                                        ) {
                                            Text(text = "Clicca qui per Inziare")
                                            if (apri) {
                                                AppContent()
                                            }
                                        }
                                    }


                                }

                            }

                        }
                    }
                }

            }
        }
    }

    fun Context.createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val image = File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            externalCacheDir      /* directory */
        )
        return image
    }

    @Composable
    fun AppContent() {
        val context = LocalContext.current
        val file = context.createImageFile()
        val uri = FileProvider.getUriForFile(
            Objects.requireNonNull(context),
            "CIAO.JPG", file
        )

        var capturedImageUri by remember {
            mutableStateOf<Uri>(Uri.EMPTY)
        }

        val cameraLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
                capturedImageUri = uri
            }

        val permissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            if (it) {
                Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
                cameraLauncher.launch(uri)
            } else {
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

        Column(
            Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                val permissionCheckResult =
                    ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)
                if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                    cameraLauncher.launch(uri)
                } else {
                    // Request a permission
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }) {
                Text(text = "Capture Image From Camera")
            }
        }

        if (capturedImageUri.path?.isNotEmpty() == true) {
            Image(
                modifier = Modifier
                    .padding(16.dp, 8.dp),
                painter = rememberImagePainter(capturedImageUri),
                contentDescription = null
            )
        }


    }

    private fun rememberImagePainter(capturedImageUri: Uri): Painter {
        TODO("Not yet implemented")
    }

    private suspend fun getLastKnownLocation(context: Context) {

        Log.i("main activity", "entro")
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
            Log.i("main activity", "permessi validi")
            //permission success
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        user.posizione = location
                        Log.i("main activity", "location settata")
                    } else {
                        Log.i("main activity", "location non settata")
                    }

                }
        } else {
            Log.i("main activity", "permessi non settati")
        }
    }


}




