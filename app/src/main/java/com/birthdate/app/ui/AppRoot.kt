package com.birthdate.app.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.birthdate.app.data.FriendRepository
import com.birthdate.app.data.SettingsStore

private enum class Screen { HOME, EDIT, SETTINGS }

@Composable
fun AppRoot(settingsStore: SettingsStore) {
    val context = LocalContext.current.applicationContext
    val repository = remember { FriendRepository(context) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    var screen by rememberSaveable { mutableStateOf(Screen.HOME) }
    var editingId by rememberSaveable { mutableStateOf<Long?>(null) }
    var editSession by rememberSaveable { mutableStateOf(0) }

    BackHandler(enabled = screen != Screen.HOME) {
        screen = Screen.HOME
        editingId = null
    }

    AnimatedContent(
        targetState = screen,
        transitionSpec = {
            if (targetState != Screen.HOME) {
                (slideInHorizontally(tween(260)) { it } + fadeIn(tween(260)))
                    .togetherWith(slideOutHorizontally(tween(260)) { -it / 3 } + fadeOut(tween(260)))
            } else {
                (slideInHorizontally(tween(260)) { -it / 3 } + fadeIn(tween(260)))
                    .togetherWith(slideOutHorizontally(tween(260)) { it } + fadeOut(tween(260)))
            }
        },
        label = "screen"
    ) { s ->
        when (s) {
            Screen.HOME -> {
                val homeViewModel: HomeViewModel = viewModel { HomeViewModel(repository) }
                val birthdays by homeViewModel.items.collectAsStateWithLifecycle()
                val today by homeViewModel.today.collectAsStateWithLifecycle()
                HomeScreen(
                    birthdays = birthdays,
                    today = today,
                    onAdd = { editingId = null; editSession++; screen = Screen.EDIT },
                    onEdit = { id -> editingId = id; editSession++; screen = Screen.EDIT },
                    onBatchDelete = { ids -> homeViewModel.deleteAll(ids) },
                    onOpenSettings = { screen = Screen.SETTINGS }
                )
            }
            Screen.EDIT -> {
                val editViewModel: EditFriendViewModel = viewModel(key = "edit_$editSession") {
                    EditFriendViewModel(repository, editingId)
                }
                EditFriendScreen(
                    viewModel = editViewModel,
                    onBack = { screen = Screen.HOME; editingId = null }
                )
            }
            Screen.SETTINGS -> {
                SettingsScreen(
                    settingsStore = settingsStore,
                    onBack = { screen = Screen.HOME }
                )
            }
        }
    }
}
