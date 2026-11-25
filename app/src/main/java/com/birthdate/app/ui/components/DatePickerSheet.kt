package com.birthdate.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.birthdate.app.data.CalendarType
import com.birthdate.app.logic.LunarConverter
import com.birthdate.app.logic.LunarNames

@Composable
fun DatePickerSheet(
    inputType: CalendarType,
    currentYear: Int,
    initialYear: Int?,
    initialMonth: Int,
    initialDay: Int,
    initialLeap: Boolean,
    onConfirm: (year: Int?, month: Int, day: Int, leap: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var year by remember { mutableStateOf(initialYear) }
    var month by remember { mutableStateOf(initialMonth) }
    var day by remember { mutableStateOf(initialDay) }
    var leap by remember { mutableStateOf(initialLeap) }

    val isLunar = inputType == CalendarType.LUNAR

    val yearOptions: List<Int?> = buildList {
        if (isLunar) add(null)
        for (y in currentYear downTo 1900) add(y)
    }
    val yearLabels = yearOptions.map { it?.let { y -> "${y}年" } ?: "不填" }

    val monthLabels = (1..12).map { if (isLunar) LunarNames.monthName(it) else "${it}月" }

    val dayCount = dayCountFor(inputType, month, year)
    val dayLabels = (1..dayCount).map { if (isLunar) LunarNames.dayName(it) else "${it}日" }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            Surface(
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp)
                        .padding(top = 24.dp, bottom = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        if (isLunar) "选择农历生日" else "选择公历出生日期",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(20.dp))

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        WheelPicker(
                            items = yearLabels,
                            initialIndex = yearOptions.indexOf(year).coerceAtLeast(0),
                            onSelected = {
                                year = yearOptions.getOrNull(it)
                                val count = dayCountFor(inputType, month, year)
                                if (day > count) day = count
                            },
                            modifier = Modifier.weight(1f)
                        )
                        WheelPicker(
                            items = monthLabels,
                            initialIndex = month - 1,
                            onSelected = {
                                month = it + 1
                                val count = dayCountFor(inputType, month, year)
                                if (day > count) day = count
                            },
                            modifier = Modifier.weight(1f)
                        )
                        key(inputType, month, year) {
                            WheelPicker(
                                items = dayLabels,
                                initialIndex = (day - 1).coerceIn(0, (dayLabels.size - 1).coerceAtLeast(0)),
                                onSelected = { day = it + 1 },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    if (isLunar) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("闰月", style = MaterialTheme.typography.bodyLarge)
                            Spacer(Modifier.weight(1f))
                            Switch(checked = leap, onCheckedChange = { leap = it })
                        }
                    }

                    if (!isLunar && year != null) {
                        LunarConverter.lunarOfSolar(year!!, month, day)?.let { preview ->
                            val leapPrefix = if (preview.isLeap) "闰" else ""
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "将保存为 农历$leapPrefix${LunarNames.monthName(preview.month)}${LunarNames.dayName(preview.day)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { onConfirm(year, month, day, leap) },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("确定") }
                }
            }
        }
    }
}

private fun dayCountFor(type: CalendarType, month: Int, year: Int?): Int = when (type) {
    CalendarType.SOLAR -> when (month) {
        2 -> if (isLeapYear(year ?: 2001)) 29 else 28
        4, 6, 9, 11 -> 30
        else -> 31
    }
    CalendarType.LUNAR -> 30
}

private fun isLeapYear(year: Int): Boolean =
    (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
