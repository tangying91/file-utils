package dev.tang.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {

    val currentTime: String
        get() {
            val now = System.currentTimeMillis()
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            return format.format(Date(now))
        }

    fun timeToDate(time: Long): String {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return format.format(Date(time))
    }

    fun dateToTime(str: String): Long {
        return SimpleDateFormat("yyyy-MM-dd").parse(str).time
    }
}