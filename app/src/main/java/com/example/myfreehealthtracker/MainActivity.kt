@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.myfreehealthtracker

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.example.myfreehealthtracker.ui.theme.Background
import com.example.myfreehealthtracker.ui.theme.MyFreeHealthTrackerTheme

data class BottomNavigationItem(
    var title: String,
    var selectedIcon: ImageVector,
    var unSelectedIcon: ImageVector,
    var hasNews: Boolean
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyFreeHealthTrackerTheme {
                // A surface container using the 'background' color from the theme
                LowerScreen()
            }
        }
    }
}

@Composable
fun TopBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.2f)
            .background(color = Background)

    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LowerScreen() {
    var selecteItemIndex by rememberSaveable {
        mutableStateOf(0)
    }
    val item = listOf(
        BottomNavigationItem(
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            unSelectedIcon = Icons.Outlined.Home,
            hasNews = true
        ),
        BottomNavigationItem(
            title = "Sport",
            selectedIcon = Icons.Filled.Favorite,
            unSelectedIcon = Icons.Outlined.CheckCircle,
            hasNews = true
        ),
        BottomNavigationItem(
            title = "Food",
            selectedIcon = Icons.Filled.LocationOn,
            unSelectedIcon = Icons.Outlined.LocationOn,
            hasNews = true
        )
    )
    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier.fillMaxHeight(0.1f)
            ) {
                item.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = (false),
                        onClick = { selecteItemIndex = index },
                        icon = {
                            BadgedBox(badge = {

                            }) {
                                Icon(
                                    imageVector = if (index == selecteItemIndex) {
                                        item.selectedIcon
                                    } else item.unSelectedIcon,
                                    contentDescription = "home"

                                )
                            }
                        })
                }
            }
        }
    ) {

    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyFreeHealthTrackerTheme {
        LowerScreen()

    }
}