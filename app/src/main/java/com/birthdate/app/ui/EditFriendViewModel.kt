package com.birthdate.app.ui

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.birthdate.app.data.CalendarType
import com.birthdate.app.data.Friend
import com.birthdate.app.data.FriendRepository
import com.birthdate.app.logic.LunarConverter
import com.birthdate.app.logic.LunarDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate

class EditFriendViewModel(
    private val repository: FriendRepository,
    private val friendId: Long?
) : ViewModel() {

    private val currentYear = LocalDate.now().year

    var name by mutableStateOf("")
        private set
    var inputType by mutableStateOf(CalendarType.SOLAR)
        private set
    var birthYear by mutableStateOf<Int?>(null)
        private set
    var month by mutableStateOf(1)
        private set
    var day by mutableStateOf(1)
        private set
    var isLeapMonth by mutableStateOf(false)
        private set

    private var existingAvatarPath: String? = null
    var avatarUri by mutableStateOf<Uri?>(null)
        private set
    private var avatarRemoved = false

    val isEditing: Boolean get() = friendId != null
    val canSave: Boolean get() = name.isNotBlank()

    init {
        val existing = friendId?.let { repository.getFriend(it) }
        if (existing != null) {
            name = existing.name
            inputType = CalendarType.LUNAR
            birthYear = existing.birthYear
            month = existing.lunarMonth
            day = existing.lunarDay
            isLeapMonth = existing.isLeapMonth
            existingAvatarPath = existing.avatarPath
        } else {
            inputType = CalendarType.SOLAR
            birthYear = currentYear
        }
    }

    fun onNameChange(value: String) { name = value }

    fun onInputTypeChange(type: CalendarType) {
        if (type == inputType) return
        inputType = type
        if (type == CalendarType.SOLAR && birthYear == null) {
            birthYear = currentYear
        }
        val maxDay = dayRange().last
        if (day > maxDay) day = maxDay
    }

    fun onBirthYearChange(value: Int?) { birthYear = value }

    fun onMonthChange(value: Int) {
        month = value
        val maxDay = dayRange().last
        if (day > maxDay) day = maxDay
    }

    fun onDayChange(value: Int) { day = value }
    fun onLeapMonthChange(value: Boolean) { isLeapMonth = value }

    fun onDateConfirmed(year: Int?, month: Int, day: Int, leap: Boolean) {
        birthYear = year
        this.month = month
        this.day = day
        isLeapMonth = leap
    }

    fun onAvatarPicked(uri: Uri) {
        avatarUri = uri
        avatarRemoved = false
    }

    fun onAvatarRemoved() {
        avatarUri = null
        avatarRemoved = true
    }

    fun displayImageModel(): Any? = when {
        avatarUri != null -> avatarUri
        avatarRemoved -> null
        existingAvatarPath != null -> File(existingAvatarPath!!)
        else -> null
    }

    fun monthOptions(): List<Int> = (1..12).toList()

    fun lunarPreview(): LunarDate? {
        if (inputType != CalendarType.SOLAR) return null
        val year = birthYear ?: return null
        return LunarConverter.lunarOfSolar(year, month, day)
    }

    fun dayRange(): IntRange = when (inputType) {
        CalendarType.SOLAR -> when (month) {
            2 -> 1..(if (isLeapYear(birthYear ?: 2001)) 29 else 28)
            4, 6, 9, 11 -> 1..30
            else -> 1..31
        }
        CalendarType.LUNAR -> 1..30
    }

    fun yearOptions(): List<Int?> = buildList {
        if (inputType == CalendarType.LUNAR) add(null)
        for (y in currentYear downTo 1900) add(y)
    }

    fun save(onDone: () -> Unit) {
        if (!canSave) return
        viewModelScope.launch(Dispatchers.IO) {
            val finalAvatar = resolveAvatarPath()

            val lunarMonth: Int
            val lunarDay: Int
            val leap: Boolean
            val year: Int?

            if (inputType == CalendarType.SOLAR) {
                val converted = LunarConverter.lunarOfSolar(birthYear ?: currentYear, month, day)
                    ?: return@launch
                lunarMonth = converted.month
                lunarDay = converted.day
                leap = converted.isLeap
                year = birthYear
            } else {
                lunarMonth = month
                lunarDay = day
                leap = isLeapMonth
                year = birthYear
            }

            val id = friendId
            if (id == null) {
                repository.addFriend(name, year, lunarMonth, lunarDay, leap, finalAvatar)
            } else {
                val existing = repository.getFriend(id)
                repository.updateFriend(
                    Friend(
                        id = id,
                        name = name.trim(),
                        avatarPath = finalAvatar,
                        birthYear = year,
                        lunarMonth = lunarMonth,
                        lunarDay = lunarDay,
                        isLeapMonth = leap,
                        createdAt = existing?.createdAt ?: System.currentTimeMillis()
                    )
                )
            }
            onDone()
        }
    }

    fun delete() {
        friendId?.let { repository.deleteFriend(it) }
    }

    private fun resolveAvatarPath(): String? {
        val newUri = avatarUri
        return when {
            newUri != null -> {
                val newPath = repository.copyAvatar(newUri)
                if (existingAvatarPath != null && existingAvatarPath != newPath) {
                    repository.deleteAvatarFile(existingAvatarPath)
                }
                newPath
            }
            avatarRemoved -> {
                if (existingAvatarPath != null) repository.deleteAvatarFile(existingAvatarPath)
                null
            }
            else -> existingAvatarPath
        }
    }

    private fun isLeapYear(year: Int): Boolean =
        (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
}
