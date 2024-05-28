package com.example.myfreehealthtracker.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import coil.compose.AsyncImage
import com.example.myfreehealthtracker.ImageController


class SportFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        return ComposeView(requireContext()).apply {
            setContent {
                val imageController = ImageController()
                Text(text = "Hello World!")
                AsyncImage(
                    model = imageController.getImageFromInternalStorage(
                        LocalContext.current,
                        "pictureProfile.png"
                    ), contentDescription = "", modifier = Modifier
                        .padding(4.dp)
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

        }
    }

}