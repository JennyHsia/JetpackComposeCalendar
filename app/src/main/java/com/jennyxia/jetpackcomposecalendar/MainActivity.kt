package com.jennyxia.jetpackcomposecalendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.ViewModelProvider
import com.jennyxia.jetpackcomposecalendar.view.CalendarApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = ViewModelProvider(this)[CalendarViewModel::class.java]
        setContent {
            MaterialTheme {
                CalendarApp(viewModel)
            }
        }
    }
}