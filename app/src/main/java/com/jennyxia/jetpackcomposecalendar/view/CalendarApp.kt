package com.jennyxia.jetpackcomposecalendar.view

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jennyxia.jetpackcomposecalendar.CalendarViewModel
import com.jennyxia.jetpackcomposecalendar.model.Event
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun CalendarApp(viewModel: CalendarViewModel) {
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var isWeekView by remember { mutableStateOf(false) }
    val focusMonth by viewModel.focusMonth.observeAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        focusMonth?.let {
            TopAppBar(
                modifier = Modifier.height(50.dp)
                    .clickable {
                        isWeekView = false
                        selectedDate = null
                    },
                title = {
                    Text(
                        modifier = Modifier.padding(top = 16.dp),
                        text = it.format(DateTimeFormatter.ofPattern("yyyy MMMM", Locale.ENGLISH)),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }

        AnimatedContent(
            modifier = Modifier.weight(1f),
            targetState = isWeekView,
            transitionSpec = {
                slideInVertically { it } + fadeIn() with
                        slideOutVertically { -it } + fadeOut()
            }
        ) { weekView ->
            if (weekView) {
                selectedDate?.let { date ->
                    WeekViewWithDetails(
                        initialDate = date,
                        viewModel = viewModel
                    )
                }
            } else {
                CalendarView(viewModel) { date ->
                    selectedDate = date
                    isWeekView = true
                }
            }
        }

    }
}

@Composable
fun WeekViewWithDetails(
    initialDate: LocalDate,
    viewModel: CalendarViewModel,
) {
    var selectedDate by remember { mutableStateOf(initialDate) }
    val events by viewModel.events.observeAsState(emptyMap())
    LaunchedEffect(Unit) {
        viewModel.loadEvents()
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        WeekView(selectedDate = selectedDate, onDateSelected = { selectedDate = it })

        EventDetails(
            date = selectedDate,
            events = events[selectedDate] ?: emptyList(),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun WeekView(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
) {
    val startOfWeek = selectedDate.minusDays(selectedDate.dayOfWeek.value % 7L)

    AnimatedVisibility(
        visible = true,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(300)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(7) { dayOffset ->
                    val date = startOfWeek.plusDays(dayOffset.toLong())
                    val isSelected = date == selectedDate

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if (!isSelected) {
                                    onDateSelected(date)
                                }
                            }
                    ) {
                        Text(
                            text = date.dayOfWeek.getDisplayName(
                                TextStyle.NARROW,
                                Locale.getDefault()
                            ),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )

                        Text(
                            text = date.dayOfMonth.toString(),
                            fontSize = 16.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else Color.Black,
                            modifier = Modifier
                                .padding(4.dp)
                                .let { mod ->
                                    if (isSelected) {
                                        mod
                                            .background(
                                                Color(0xFF2196F3),
                                                RoundedCornerShape(12.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    } else mod
                                }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedTransitionScreen(modifier: Modifier) {
    var showOtherViews by remember { mutableStateOf(true) }
    var moveToTop by remember { mutableStateOf(false) }
    var showNewView by remember { mutableStateOf(false) }

    val offsetY by animateDpAsState(
        targetValue = if (moveToTop) 0.dp else 500.dp,
        animationSpec = tween(durationMillis = 600),
        finishedListener = {
            // 动画完成后展示新 View
            showNewView = true
        }
    )

    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(16.dp))

        // View A
        Box(
            modifier = Modifier
                .offset(y = offsetY)
                .fillMaxWidth()
                .height(100.dp)
                .background(Color.Blue)
                .clickable {
                    // 点击触发动画
                    showOtherViews = false
                    moveToTop = true
                },
            contentAlignment = Alignment.Center
        ) {
            Text("View A", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 其他 View
        AnimatedVisibility(visible = showOtherViews) {
            Column {
                Text("Other View 1")
                Text("Other View 2")
            }
        }

        // 新 View B
        AnimatedVisibility(visible = showNewView) {
            Column {
                Spacer(modifier = Modifier.height(16.dp))
                Text("New View B", color = Color.Green)
            }
        }
    }
}

