package com.example.myfreehealthtracker

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import androidx.core.app.NotificationCompat

enum class Actions {
    START, STOP
}

class SportActivityService : Service(), SensorEventListener {


    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null
    private var stepCount: Int = 0


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when (intent?.action) {
            Actions.START.toString() -> startForegroundService()
            Actions.STOP.toString() -> stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }


    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onSensorChanged(event: SensorEvent?) {
        TODO("Not yet implemented")
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Handle sensor accuracy changes if needed
    }

    @SuppressLint("ForegroundServiceType")
    private fun startForegroundService() {
        val notification = NotificationCompat.Builder(this, "running_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground).setContentTitle("Running")
            .setContentText("Elapsed Time : 00:00").build()
        startForeground(1, notification)

    }
}