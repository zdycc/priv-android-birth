package com.birthdate.app.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.birthdate.app.logic.LunarNames
import com.birthdate.app.ui.BirthdayItem
import java.io.File

@Composable
fun FriendCard(
    item: BirthdayItem,
    onClick: () -> Unit,
    selectionMode: Boolean = false,
    selected: Boolean = false
) {
    val isToday = item.next.daysUntil == 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .then(
                if (isToday) {
                    Modifier.border(1.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selectionMode) {
                Checkbox(checked = selected, onCheckedChange = null)
                Spacer(Modifier.width(8.dp))
            }
            Avatar(
                name = item.friend.name,
                imageModel = item.friend.avatarPath?.let { File(it) }
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isToday) {
                        Text("🎂", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.width(4.dp))
                    }
                    Text(
                        text = item.friend.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                    if (item.next.age != null) {
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "${item.next.age}岁",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(Modifier.height(3.dp))
                Text(
                    text = nextBirthdayText(item),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            DaysBadge(item.next.daysUntil)
        }
    }
}

@Composable
private fun DaysBadge(days: Int) {
    val isToday = days == 0
    val container = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val content = if (isToday) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    Surface(
        color = container,
        contentColor = content,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = daysText(days),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelLarge
        )
    }
}

private fun daysText(days: Int): String = when (days) {
    0 -> "今天"
    1 -> "明天"
    else -> "还剩 $days 天"
}

private fun nextBirthdayText(item: BirthdayItem): String {
    val f = item.friend
    val d = item.next.date
    val leap = if (f.isLeapMonth) "闰" else ""
    return "农历$leap${LunarNames.monthName(f.lunarMonth)}${LunarNames.dayName(f.lunarDay)} · ${d.monthValue}月${d.dayOfMonth}日"
}
