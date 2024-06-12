package com.example.myfreehealthtracker

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class Clock {
    private var counterValue: Int = 0
    private var isRunning: Boolean = false
    fun start() {
        isRunning = true
    }

    fun getValue(): Flow<Int> = flow {
        while (isRunning) {
            emit(counterValue)
            delay(1000)
            counterValue++
        }
    }

    fun stop() {
        isRunning = false
    }
}
