package com.birthdate.app.logic

import com.birthdate.app.data.Friend
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class NextBirthday(
    val date: LocalDate,
    val daysUntil: Int,
    val age: Int?
)

object BirthdayCalculator {

    fun nextBirthday(friend: Friend, today: LocalDate): NextBirthday {
        val thisYear = LunarConverter.solarDateOf(today.year, friend.lunarMonth, friend.lunarDay, friend.isLeapMonth)
        val date = when {
            thisYear == null -> LunarConverter.solarDateOf(today.year + 1, friend.lunarMonth, friend.lunarDay, friend.isLeapMonth)
            thisYear < today -> LunarConverter.solarDateOf(today.year + 1, friend.lunarMonth, friend.lunarDay, friend.isLeapMonth)
            else -> thisYear
        } ?: LocalDate.of(today.year + 1, 1, 1)

        val daysUntil = ChronoUnit.DAYS.between(today, date).toInt()
        return NextBirthday(date, daysUntil, currentAge(friend, today))
    }

    fun sortByUpcoming(friends: List<Friend>, today: LocalDate): List<Friend> =
        friends.sortedWith(
            compareBy<Friend> { nextBirthday(it, today).daysUntil }
                .thenBy { it.name }
        )

    fun currentAge(friend: Friend, today: LocalDate): Int? {
        val year = friend.birthYear ?: return null
        val birthday = LunarConverter.solarDateOf(today.year, friend.lunarMonth, friend.lunarDay, friend.isLeapMonth)
        val notYetThisYear = birthday != null && birthday > today
        return (today.year - year - (if (notYetThisYear) 1 else 0)).takeIf { it >= 0 }
    }
}
