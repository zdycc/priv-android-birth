package com.birthdate.app.logic

import com.nlf.calendar.Lunar
import com.nlf.calendar.LunarYear
import com.nlf.calendar.Solar
import java.time.LocalDate
import kotlin.math.abs

data class LunarDate(val month: Int, val day: Int, val isLeap: Boolean)

object LunarConverter {

    /**
     * 把某年的农历（月/日/是否闰月）转成公历日期。
     * - 闰月在该年不存在时回退到平月；仍无法转换则返回 null。
     * - 若该农历月天数小于传入的日（如二月只有 29 天却传了 30），自动取该月最后一天。
     */
    fun solarDateOf(lunarYear: Int, month: Int, day: Int, isLeapMonth: Boolean): LocalDate? =
        convert(lunarYear, month, day, isLeapMonth)
            ?: if (isLeapMonth) convert(lunarYear, month, day, false) else null

    /**
     * 把公历日期（含年份）换算成农历月/日（含是否闰月）。
     */
    fun lunarOfSolar(solarYear: Int, month: Int, day: Int): LunarDate? = try {
        val lunar = Solar.fromYmd(solarYear, month, day).lunar
        LunarDate(
            month = abs(lunar.month),
            day = lunar.day,
            isLeap = lunar.month < 0
        )
    } catch (e: Exception) {
        null
    }

    private fun convert(lunarYear: Int, month: Int, day: Int, isLeapMonth: Boolean): LocalDate? {
        val signedMonth = if (isLeapMonth) -month else month
        return try {
            val lunarMonth = LunarYear.fromYear(lunarYear).getMonth(signedMonth) ?: return null
            val effectiveDay = day.coerceAtMost(lunarMonth.dayCount)
            val solar = Lunar(lunarYear, signedMonth, effectiveDay).solar
            LocalDate.of(solar.year, solar.month, solar.day)
        } catch (e: Exception) {
            null
        }
    }
}
