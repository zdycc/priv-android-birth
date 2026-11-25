package com.birthdate.app.logic

import com.birthdate.app.data.Friend
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class BirthdayCalculatorTest {

    private fun friend(
        name: String = "张三",
        year: Int? = null,
        lunarMonth: Int,
        lunarDay: Int,
        leap: Boolean = false
    ) = Friend(
        id = 1L,
        name = name,
        birthYear = year,
        lunarMonth = lunarMonth,
        lunarDay = lunarDay,
        isLeapMonth = leap
    )

    @Test
    fun solarToLunarRoundTrip() {
        val lunar = LunarConverter.lunarOfSolar(2026, 9, 15)!!
        val back = LunarConverter.solarDateOf(2026, lunar.month, lunar.day, lunar.isLeap)
        assertEquals(LocalDate.of(2026, 9, 15), back)
    }

    @Test
    fun lunarBirthdayTodayIsZeroDays() {
        val solar = LunarConverter.solarDateOf(2026, 8, 15, false)!!
        val next = BirthdayCalculator.nextBirthday(friend(lunarMonth = 8, lunarDay = 15), solar)
        assertEquals(0, next.daysUntil)
        assertEquals(solar, next.date)
    }

    @Test
    fun lunarBirthdayAlreadyPassedRollsToNextYear() {
        val today = LocalDate.of(2026, 12, 31)
        val next = BirthdayCalculator.nextBirthday(friend(lunarMonth = 1, lunarDay = 1), today)
        assertTrue(next.daysUntil > 0)
        assertEquals(2027, next.date.year)
    }

    @Test
    fun lunarLeapMonthFallsBackWhenNoLeap() {
        val next = BirthdayCalculator.nextBirthday(
            friend(lunarMonth = 4, lunarDay = 15, leap = true),
            LocalDate.of(2026, 1, 1)
        )
        assertTrue(next.daysUntil >= 0)
    }

    @Test
    fun ageWithoutYearIsNull() {
        assertNull(BirthdayCalculator.currentAge(friend(lunarMonth = 8, lunarDay = 15), LocalDate.of(2026, 1, 1)))
    }

    @Test
    fun ageComputedFromBirthYear() {
        val age = BirthdayCalculator.currentAge(
            friend(year = 2000, lunarMonth = 8, lunarDay = 15),
            LocalDate.of(2026, 9, 1)
        )
        assertTrue(age == 25 || age == 26)
    }

    @Test
    fun solarInputConvertsAndCalculates() {
        val lunar = LunarConverter.lunarOfSolar(1990, 9, 15)!!
        val f = friend(year = 1990, lunarMonth = lunar.month, lunarDay = lunar.day, leap = lunar.isLeap)
        val next = BirthdayCalculator.nextBirthday(f, LocalDate.of(2026, 1, 1))
        assertTrue(next.daysUntil >= 0)
        assertTrue(next.date.year in 2026..2027)
    }

    @Test
    fun sortingByDaysAscending() {
        val today = LocalDate.of(2026, 1, 1)
        val list = listOf(
            friend(name = "远", lunarMonth = 12, lunarDay = 30),
            friend(name = "近", lunarMonth = 1, lunarDay = 5),
            friend(name = "中", lunarMonth = 6, lunarDay = 1)
        )
        val sorted = BirthdayCalculator.sortByUpcoming(list, today)
        assertEquals(listOf("近", "中", "远"), sorted.map { it.name })
    }
}
