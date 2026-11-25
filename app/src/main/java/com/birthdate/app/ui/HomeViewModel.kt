package com.birthdate.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.birthdate.app.data.Friend
import com.birthdate.app.data.FriendRepository
import com.birthdate.app.logic.BirthdayCalculator
import com.birthdate.app.logic.NextBirthday
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

data class BirthdayItem(val friend: Friend, val next: NextBirthday)

class HomeViewModel(private val repository: FriendRepository) : ViewModel() {

    private val _today = MutableStateFlow(LocalDate.now())
    val today: StateFlow<LocalDate> = _today.asStateFlow()

    init {
        viewModelScope.launch {
            while (true) {
                val now = LocalDateTime.now()
                val nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay()
                delay(Duration.between(now, nextMidnight).toMillis())
                _today.value = LocalDate.now()
            }
        }
    }

    val items: StateFlow<List<BirthdayItem>> = combine(repository.friends, _today) { list, today ->
        list.map { BirthdayItem(it, BirthdayCalculator.nextBirthday(it, today)) }
            .sortedWith(
                compareBy<BirthdayItem> { it.next.daysUntil }
                    .thenBy { it.friend.name }
            )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun deleteAll(ids: Collection<Long>) = repository.deleteFriends(ids)
}
