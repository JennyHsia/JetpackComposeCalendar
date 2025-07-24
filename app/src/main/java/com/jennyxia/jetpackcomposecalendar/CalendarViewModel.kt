package com.jennyxia.jetpackcomposecalendar

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jennyxia.jetpackcomposecalendar.model.Event
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class CalendarViewModel: ViewModel() {
    val sampleEvents = mapOf(
        LocalDate.of(2025, 7, 15) to listOf(
            Event(
                "1",
                "Meeting",
                "09:00",
                "Team weekly meeting to discuss project progress",
                Color(0xFF4CAF50)
            ),
            Event("2", "Lunch", "12:00", "Business lunch with client", Color(0xFF2196F3)),
            Event("3", "Workout", "18:00", "Strength training at the gym", Color(0xFFFF9800))
        ),
        LocalDate.of(2025, 7, 20) to listOf(
            Event("4", "Birthday", "All day", "Friend's birthday party", Color(0xFFE91E63)),
            Event("5", "Shopping", "14:00", "Shopping for new clothes at the mall", Color(0xFF9C27B0))
        ),
        LocalDate.of(2025, 7, 25) to listOf(
            Event("6", "Date", "19:00", "Dinner date", Color(0xFFFF5722))
        )
    )

    var selectedDate = mutableStateOf(LocalDate.now())

    var currentMonth = mutableStateOf(YearMonth.now())

    val visibleMonths: List<YearMonth> = (-24..24).map {
        YearMonth.now().plusMonths(it.toLong())
    }

    val currentMonthIndex = visibleMonths.indexOf(currentMonth.value)

    private val _focusMonth = MutableLiveData<YearMonth>(YearMonth.now())
    val focusMonth: LiveData<YearMonth> = _focusMonth
    private val _events = MutableLiveData<Map<LocalDate, List<Event>>>(emptyMap())
    val events: LiveData<Map<LocalDate, List<Event>>> = _events

    fun selectDate(date: LocalDate) {
        selectedDate.value = date
    }

    fun updateFocusMonth(month: YearMonth) {
        _focusMonth.value = month
    }

    fun loadEvents() {
        viewModelScope.launch {
            _events.value = sampleEvents
        }
    }
}