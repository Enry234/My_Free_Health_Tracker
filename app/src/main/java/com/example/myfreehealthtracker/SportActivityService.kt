package com.example.myfreehealthtracker

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

enum class Actions {
    START, STOP
}

class SportActivityService : Service() {
    companion object {
        const val ACTION_COUNTER_UPDATE = "com.example.servicetest.COUNTER_UPDATE"
        const val EXTRA_COUNTER_VALUE = "com.example.servicetest.EXTRA_COUNTER_VALUE"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private val clock = Clock()
    private var value: Int = 0
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Actions.START.toString() -> start()
            Actions.STOP.toString() -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun stop() {
        clock.stop()
        stopSelf()
    }

    private fun start() {
        CoroutineScope(Dispatchers.Default).launch {
            clock.start().collect { counterValue ->
                Log.i("counter", counterValue.toString())
                value = counterValue
                if (value % 60 == 0) {
                    notification(counterValue.toString())
                }
                sendCounterUpdate(counterValue)
            }
        }
    }

    private fun sendCounterUpdate(counterValue: Int) {
        val intent = Intent(ACTION_COUNTER_UPDATE).apply {
            putExtra(EXTRA_COUNTER_VALUE, counterValue)
        }
        sendBroadcast(intent)
    }

    private fun notification(counterValue: String) {
        val counterNotification = NotificationCompat.Builder(this, "counter_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground).setContentText("Attivita' in Corso")
            .setContentTitle("Tempo trascorso: " + counterValue.toString() + "min").build()
        startForeground(1, counterNotification)
    }

}