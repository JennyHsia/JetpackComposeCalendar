package com.jennyxia.jetpackcomposecalendar.model

import androidx.compose.ui.graphics.Color

data class Event(
    val id: String,
    val title: String,
    val time: String,
    val description: String,
    val color: Color
)