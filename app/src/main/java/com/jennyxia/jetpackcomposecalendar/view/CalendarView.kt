package com.jennyxia.jetpackcomposecalendar.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jennyxia.jetpackcomposecalendar.CalendarViewModel
import com.jennyxia.jetpackcomposecalendar.model.Event
import kotlinx.coroutines.flow.distinctUntilChanged
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarView(
    viewModel: CalendarViewModel,
    onDateClick: (LocalDate) -> Unit
) {
    val lazyListState = rememberLazyListState()
    val visibleMonths = viewModel.visibleMonths
    val currentMonthIndex = viewModel.currentMonthIndex
    val events by viewModel.events.observeAsState(emptyMap())

    LaunchedEffect(visibleMonths) {
        if (currentMonthIndex >= 0) {
            lazyListState.scrollToItem(currentMonthIndex)
        }
    }
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemIndex }
            .distinctUntilChanged()
            .collect { index ->
                visibleMonths.getOrNull(index)?.let {
                    viewModel.updateFocusMonth(it)
                }
            }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        WeekHeader()

        LazyColumn(
            state = lazyListState,
            modifier = Modifier.weight(1f)
        ) {
            items(visibleMonths) { month ->
                MonthView(
                    currentMonth = month,
                    events = events,
                    onDateClick = onDateClick
                )
            }
        }
    }
}

@Composable
fun WeekHeader() {
    Row(
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val weekdays = listOf("S", "M", "T", "W", "T", "F", "S")
        weekdays.forEach { day ->
            Text(
                text = day,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun MonthView(
    currentMonth: YearMonth,
    events: Map<LocalDate, List<Event>>,
    onDateClick: (LocalDate) -> Unit
) {
    val firstDayOfMonth = currentMonth.atDay(1)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
    val daysInMonth = currentMonth.lengthOfMonth()

    LazyColumn(
        modifier = Modifier
            .height(500.dp)
            .fillMaxWidth()
            .background(Color.White)
    ) {
        val totalCells = firstDayOfWeek + daysInMonth
        val weeks = (totalCells + 6) / 7

        items(weeks) { week ->
            WeekRow(
                week = week,
                firstDayOfWeek = firstDayOfWeek,
                daysInMonth = daysInMonth,
                currentMonth = currentMonth,
                events = events,
                onDateClick = onDateClick
            )
        }
    }
}

@Composable
fun WeekRow(
    week: Int,
    firstDayOfWeek: Int,
    daysInMonth: Int,
    currentMonth: YearMonth,
    events: Map<LocalDate, List<Event>>,
    onDateClick: (LocalDate) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        repeat(7) { dayOfWeek ->
            val dayNumber = week * 7 + dayOfWeek - firstDayOfWeek + 1

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(2.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .clickable(enabled = dayNumber in 1..daysInMonth) {
                        if (dayNumber in 1..daysInMonth) {
                            onDateClick(currentMonth.atDay(dayNumber))
                        }
                    },
                contentAlignment = Alignment.TopCenter
            ) {
                if (dayNumber in 1..daysInMonth) {
                    val date = currentMonth.atDay(dayNumber)
                    val dayEvents = events[date] ?: emptyList()
                    val isToday = date == LocalDate.now()

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = dayNumber.toString(),
                            fontSize = 16.sp,
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                            color = when {
                                isToday -> Color.White
                                dayOfWeek == 0 -> Color.Red
                                else -> Color.Black
                            },
                            modifier = Modifier
                                .padding(4.dp)
                                .let { mod ->
                                    if (isToday) {
                                        mod
                                            .background(
                                                Color(0xFF2196F3),
                                                RoundedCornerShape(12.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    } else mod
                                }
                        )

                        dayEvents.take(2).forEach { event ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(12.dp)
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                                    .background(
                                        event.color.copy(alpha = 0.3f),
                                        RoundedCornerShape(2.dp)
                                    )
                            )
                        }

                        if (dayEvents.size > 2) {
                            Text(
                                text = "+${dayEvents.size - 2}",
                                fontSize = 8.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}
