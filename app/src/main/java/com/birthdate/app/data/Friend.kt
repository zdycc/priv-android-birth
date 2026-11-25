package com.birthdate.app.data

enum class CalendarType { SOLAR, LUNAR }

data class Friend(
    val id: Long,
    val name: String,
    val avatarPath: String? = null,
    val birthYear: Int? = null,
    val lunarMonth: Int,
    val lunarDay: Int,
    val isLeapMonth: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
