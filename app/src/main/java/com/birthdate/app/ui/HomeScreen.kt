package com.birthdate.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Cake
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.birthdate.app.logic.LunarConverter
import com.birthdate.app.logic.LunarNames
import com.birthdate.app.ui.components.FriendCard
import java.time.LocalDate

private sealed interface HomeRow {
    data class Header(val title: String) : HomeRow
    data class Friend(val item: BirthdayItem) : HomeRow
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    birthdays: List<BirthdayItem>,
    today: LocalDate,
    onAdd: () -> Unit,
    onEdit: (Long) -> Unit,
    onBatchDelete: (List<Long>) -> Unit,
    onOpenSettings: () -> Unit
) {
    val todayLabel = remember(today) {
        val lunar = LunarConverter.lunarOfSolar(today.year, today.monthValue, today.dayOfMonth)
        if (lunar != null) {
            val leap = if (lunar.isLeap) "闰" else ""
            "${today.monthValue}月${today.dayOfMonth}日 · 农历$leap${LunarNames.monthName(lunar.month)}${LunarNames.dayName(lunar.day)}"
        } else {
            "${today.monthValue}月${today.dayOfMonth}日"
        }
    }
    val daysUntilEndOfMonth = today.lengthOfMonth() - today.dayOfMonth

    var query by rememberSaveable { mutableStateOf("") }
    var selectionMode by remember { mutableStateOf(false) }
    var selectedIds by remember { mutableStateOf(emptySet<Long>()) }
    var showBatchDeleteDialog by remember { mutableStateOf(false) }

    val allIds = birthdays.map { it.friend.id }.toSet()
    val allSelected = selectedIds.isNotEmpty() && selectedIds.size == allIds.size

    Scaffold(
        topBar = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
                    .statusBarsPadding()
            ) {
                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 4.dp)
                ) {
                    Icon(
                        Icons.Outlined.Settings,
                        contentDescription = "设置",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Column(
                    Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "生日提醒",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        todayLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (birthdays.isNotEmpty()) {
                    Row(
                        Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (selectionMode) {
                            TextButton(onClick = {
                                selectedIds = if (allSelected) emptySet() else allIds
                            }) {
                                Text(if (allSelected) "取消全选" else "全选")
                            }
                        }
                        TextButton(onClick = {
                            if (selectionMode) {
                                selectionMode = false
                                selectedIds = emptySet()
                            } else {
                                selectionMode = true
                                query = ""
                                selectedIds = emptySet()
                            }
                        }) {
                            Text(if (selectionMode) "完成" else "管理")
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            if (!selectionMode) {
                FloatingActionButton(
                    onClick = onAdd,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "添加好友")
                }
            }
        },
        bottomBar = {
            if (selectionMode) {
                Surface(shadowElevation = 8.dp) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "已选 ${selectedIds.size} 位",
                            Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Button(
                            onClick = { showBatchDeleteDialog = true },
                            enabled = selectedIds.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) { Text("删除") }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (birthdays.isEmpty()) {
            EmptyState(modifier = Modifier.padding(innerPadding).fillMaxSize())
        } else {
            val filtered = if (query.isBlank()) {
                birthdays
            } else {
                birthdays.filter { it.friend.name.contains(query.trim(), ignoreCase = true) }
            }

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                if (birthdays.size > 5 && !selectionMode) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text("搜索姓名") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                Text(
                    text = "共 ${birthdays.size} 位好友",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                )
                if (filtered.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "没有匹配的好友",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    val rows = buildList {
                        var lastGroup: String? = null
                        filtered.forEach { item ->
                            val group = groupLabel(item.next.daysUntil, daysUntilEndOfMonth)
                            if (group != lastGroup) {
                                add(HomeRow.Header(group))
                                lastGroup = group
                            }
                            add(HomeRow.Friend(item))
                        }
                    }
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(rows, key = { row ->
                            when (row) {
                                is HomeRow.Header -> "header_${row.title}"
                                is HomeRow.Friend -> "friend_${row.item.friend.id}"
                            }
                        }) { row ->
                            when (row) {
                                is HomeRow.Header -> HeaderText(row.title)
                                is HomeRow.Friend -> FriendCard(
                                    item = row.item,
                                    selectionMode = selectionMode,
                                    selected = row.item.friend.id in selectedIds,
                                    onClick = {
                                        if (selectionMode) {
                                            val id = row.item.friend.id
                                            selectedIds = if (id in selectedIds) selectedIds - id else selectedIds + id
                                        } else {
                                            onEdit(row.item.friend.id)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showBatchDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showBatchDeleteDialog = false },
            title = { Text("批量删除") },
            text = { Text("确定删除选中的 ${selectedIds.size} 位好友吗？此操作不可恢复。") },
            confirmButton = {
                TextButton(onClick = {
                    onBatchDelete(selectedIds.toList())
                    selectedIds = emptySet()
                    selectionMode = false
                    showBatchDeleteDialog = false
                }) { Text("删除", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showBatchDeleteDialog = false }) { Text("取消") }
            }
        )
    }
}

@Composable
private fun HeaderText(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 2.dp)
    )
}

private fun groupLabel(daysUntil: Int, daysUntilEndOfMonth: Int): String = when {
    daysUntil == 0 -> "今天"
    daysUntil <= 7 -> "7 天内"
    daysUntil <= daysUntilEndOfMonth -> "本月"
    else -> "更远"
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.Cake,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(Modifier.height(16.dp))
        Text("还没有好友", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(4.dp))
        Text(
            "点击右下角 + 添加第一位好友",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
