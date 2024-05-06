package com.example.myfreehealthtracker

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
class LoginPage {
    private var user: UserData = UserData()


    @SuppressLint("UnrememberedMutableState")
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
                                                        label = { Text(text = "Inserisci il tuo Nome") },
                                                        singleLine = true,
                                                        modifier = Modifier.fillMaxWidth(0.8f),
                                                        colors = if (error) {
                                                            TextFieldDefaults.colors(
                                                                unfocusedContainerColor = Color.Red.copy(
                                                                    0.4f
                                                                ),
                                                                focusedContainerColor = Color.Red.copy(
                                                                    0.4f
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
                                                        label = { Text(text = "Inserisci il tuo Cognome") },
                                                        singleLine = true,
                                                        modifier = Modifier.fillMaxWidth(0.8f),
                                                        colors = if (error) {
                                                            TextFieldDefaults.colors(
                                                                unfocusedContainerColor = Color.Red.copy(
                                                                    0.4f
                                                                ),
                                                                focusedContainerColor = Color.Red.copy(
                                                                    0.4f
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
                                        mutableStateOf("Clicca qui per inserire la data")
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
                                                        modifier = Modifier.fillMaxWidth(0.4f),
                                                        colors = if (error) {
                                                            ButtonDefaults.buttonColors(
                                                                containerColor = Color.Red.copy(0.4f)
                                                            )
                                                        } else {
                                                            ButtonDefaults.buttonColors(
                                                                containerColor = Color.Blue.copy(
                                                                    0.4f
                                                                )
                                                            )
                                                        }
                                                    ) {
                                                        Text(
                                                            text = dateText,
                                                            textAlign = TextAlign.Center
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
                                                                val format = "YYYY-MM-DD"
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
                                                    val radioOptions = listOf("M", "F", "X")
                                                    val (selectedOption, onOptionSelected) = remember {
                                                        mutableStateOf(
                                                            radioOptions[0]
                                                        )
                                                    }

                                                    Row(
                                                        horizontalArrangement = Arrangement.SpaceBetween,
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

                                                        RadioButton(
                                                            selected = ("X" == selectedOption),
                                                            onClick = {
                                                                onOptionSelected("X"); sesso = "X"
                                                            })
                                                    }
                                                    Row(
                                                        horizontalArrangement = Arrangement.SpaceBetween,
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

                                                        Text(
                                                            text = "Altro",
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
                                                        singleLine = true,
                                                        modifier = Modifier.fillMaxWidth(0.8f),
                                                        colors = if (error) {
                                                            TextFieldDefaults.colors(
                                                                unfocusedContainerColor = Color.Red.copy(
                                                                    0.4f
                                                                ),
                                                                focusedContainerColor = Color.Red.copy(
                                                                    0.4f
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
                                                        label = { Text(text = "Inserisci la tua Altezza") },
                                                        singleLine = true,
                                                        modifier = Modifier.fillMaxWidth(0.8f),
                                                        colors = if (error) {
                                                            TextFieldDefaults.colors(
                                                                unfocusedContainerColor = Color.Red.copy(
                                                                    0.4f
                                                                ),
                                                                focusedContainerColor = Color.Red.copy(
                                                                    0.4f
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
                                                user
                                            },
                                            modifier = Modifier.hoverable(interactionSource = interactionSource)
                                        ) {
                                            Text(text = "Clicca qui per Inziare")
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



