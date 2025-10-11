package me.cdh

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext

class CoroutineTimer(
    private val intervalMs: Long,
    private val onTick: CoroutineTimer.() -> Unit
) {
    private var job: Job? = null
    private var isActive = false

    fun start() {
        if (job?.isActive == true) return
        isActive = true
        job = CoroutineScope(EmptyCoroutineContext).launch {
            while (isActive) {
                onTick()
                delay(intervalMs)
            }
        }
    }

    fun pause() {
        isActive = false
        job?.cancel()
        job = null
    }

    fun stop() {
        isActive = false
        job?.cancel()
    }
}