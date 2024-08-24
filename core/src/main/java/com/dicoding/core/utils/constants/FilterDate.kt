package com.dicoding.membership.core.utils.constants

import com.dicoding.membership.core.utils.DateUtils

enum class FilterDate(val value: String, val display: String) {
    NOW(DateUtils.getDateThisDay(), "Hari Ini"),
    MONTH(DateUtils.getDateThisMonth(), "Bulan Ini"),
    YEAR(DateUtils.getDateThisYear(), "Tahun Ini")
}

val filterDateList =
    listOf(FilterDate.NOW.display, FilterDate.MONTH.display, FilterDate.YEAR.display)

fun mapToFilterDateValue(display: String): String {
    return when (display) {
        FilterDate.NOW.display -> {
            FilterDate.NOW.value
        }
        FilterDate.MONTH.display -> {
            FilterDate.MONTH.value
        }
        FilterDate.YEAR.display -> {
            FilterDate.YEAR.value
        }
        else -> {
            FilterDate.NOW.value
        }
    }
}