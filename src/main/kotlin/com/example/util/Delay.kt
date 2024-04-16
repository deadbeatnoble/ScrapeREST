package com.example.util

import kotlinx.coroutines.delay

class Delay {
    suspend fun customDelay(second: Long = 0, minute: Long = 0, hour: Long = 0) {
        val delayTimeInMillis = ((second) + (minute * 60) + (hour * 3600)) * 1000
        delay(delayTimeInMillis)
    }
}