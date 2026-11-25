package com.birthdate.app.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.birthdate.app.data.CalendarType
import com.birthdate.app.logic.LunarNames
import com.birthdate.app.ui.components.Avatar
import com.birthdate.app.ui.components.DatePickerSheet
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditFriendScreen(
    viewModel: EditFriendViewModel,
    onBack: () -> Unit
) {
    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) viewModel.onAvatarPicked(uri) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val dateSummary = dateSummary(
        viewModel.inputType, viewModel.birthYear, viewModel.month, viewModel.day, viewModel.isLeapMonth
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (viewModel.isEditing) "编辑好友" else "添加好友") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.save(onBack) },
                        enabled = viewModel.canSave
                    ) { Text("保存") }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            AvatarPicker(
                name = viewModel.name,
                imageModel = viewModel.displayImageModel(),
                onPickImage = {
                    imagePicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                onRemove = viewModel::onAvatarRemoved
            )

            OutlinedTextField(
                value = viewModel.name,
                onValueChange = viewModel::onNameChange,
                label = { Text("姓名") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            CalendarTypeSelector(
                selected = viewModel.inputType,
                onSelect = viewModel::onInputTypeChange
            )

            Column {
                Text(
                    "出生日期",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                        .clickable { showDatePicker = true }
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            dateSummary,
                            Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Icon(
                            Icons.Outlined.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (viewModel.inputType == CalendarType.SOLAR) {
                val preview = viewModel.lunarPreview()
                if (preview != null) {
                    val leap = if (preview.isLeap) "闰" else ""
                    Text(
                        text = "将保存为 农历$leap${LunarNames.monthName(preview.month)}${LunarNames.dayName(preview.day)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (viewModel.isEditing) {
                Spacer(Modifier.height(4.dp))
                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("删除好友", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerSheet(
            inputType = viewModel.inputType,
            currentYear = LocalDate.now().year,
            initialYear = viewModel.birthYear,
            initialMonth = viewModel.month,
            initialDay = viewModel.day,
            initialLeap = viewModel.isLeapMonth,
            onConfirm = { y, m, d, leap ->
                viewModel.onDateConfirmed(y, m, d, leap)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("删除好友") },
            text = { Text("确定删除「${viewModel.name}」吗？此操作不可恢复。") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.delete()
                    onBack()
                }) { Text("删除", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("取消") }
            }
        )
    }
}

private fun dateSummary(inputType: CalendarType, year: Int?, month: Int, day: Int, leap: Boolean): String =
    when (inputType) {
        CalendarType.SOLAR -> "${year}年${month}月${day}日"
        CalendarType.LUNAR -> {
            val lunar = (if (leap) "闰" else "") + LunarNames.monthName(month) + LunarNames.dayName(day)
            year?.let { "${it}年 $lunar" } ?: lunar
        }
    }

@Composable
private fun AvatarPicker(
    name: String,
    imageModel: Any?,
    onPickImage: () -> Unit,
    onRemove: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Avatar(name = name, imageModel = imageModel, size = 96.dp)
        Spacer(Modifier.height(12.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(onClick = onPickImage) {
                Icon(Icons.Outlined.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("选择头像")
            }
            if (imageModel != null) {
                TextButton(onClick = onRemove) { Text("移除") }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarTypeSelector(
    selected: CalendarType,
    onSelect: (CalendarType) -> Unit
) {
    Column {
        Text(
            "生日类型",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
            SegmentedButton(
                selected = selected == CalendarType.SOLAR,
                onClick = { onSelect(CalendarType.SOLAR) },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
            ) { Text("公历") }
            SegmentedButton(
                selected = selected == CalendarType.LUNAR,
                onClick = { onSelect(CalendarType.LUNAR) },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
            ) { Text("农历") }
        }
    }
}
