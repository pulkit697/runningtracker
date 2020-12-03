package com.example.runtracker.runningapp.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.PendingIntent.getService
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.runtracker.R
import com.example.runtracker.common.Constants.ACTION_PAUSE_SERVICE
import com.example.runtracker.common.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.runtracker.common.Constants.ACTION_STOP_SERVICE
import com.example.runtracker.common.Constants.LOCATION_FASTEST_INTERVAL
import com.example.runtracker.common.Constants.LOCATION_UPDATE_INTERVAL
import com.example.runtracker.common.Constants.NOTIFICATION_CHANNEL_ID
import com.example.runtracker.common.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.runtracker.common.Constants.NOTIFICATION_ID
import com.example.runtracker.common.Constants.TIMER_UPDATE_INTERVAL
import com.example.runtracker.common.Utility
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

typealias polyline = MutableList<LatLng>
typealias polylines = MutableList<polyline>

@AndroidEntryPoint
class TrackingService : LifecycleService() {

    var isFirstRun = true
    var serviceKilled = false

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var baseNotificationBuilder:NotificationCompat.Builder
    lateinit var currNotificationBuilder: NotificationCompat.Builder

    private val timeRunInSeconds = MutableLiveData<Long>()
    companion object
    {
        val timeRunInMillis = MutableLiveData<Long>()
        var isTracking = MutableLiveData<Boolean>()
        var pathLines = MutableLiveData<polylines>()
    }

    override fun onCreate() {
        super.onCreate()
        currNotificationBuilder = baseNotificationBuilder
        postInitialValues()
        isTracking.observe(this, Observer {
            updateLocationTracking(it)
            updateNotification(it)
        })
    }

    private var isTimerEnabled = false
    private var lapTime = 0L
    private var timeRun = 0L
    private var timeStarted = 0L
    private var lastSecondTimeStamp = 0L

    private fun startTimer()
    {
        addEmptyList()
        isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimerEnabled = true
        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!)
            {
                lapTime = System.currentTimeMillis() - timeStarted
                timeRunInMillis.postValue(timeRun + lapTime)
                if(timeRunInMillis.value!! >= lastSecondTimeStamp+1000L)
                {
                    timeRunInSeconds.postValue(timeRunInSeconds.value!!+1)
                    lastSecondTimeStamp += 1000L
                }
                delay(TIMER_UPDATE_INTERVAL)
            }
            timeRun += lapTime
        }
    }

    fun killService()
    {
        postInitialValues()
        serviceKilled=true
        isFirstRun=true
        pauseService()
        stopForeground(true)
        stopSelf()
    }

    fun postInitialValues()
    {
        timeRunInSeconds.postValue(0L)
        timeRunInMillis.postValue(0L)
        isTracking.postValue(false)
        pathLines.postValue(mutableListOf())
    }

    fun addEmptyList() = pathLines.value?.apply {
            add(mutableListOf())
            pathLines.postValue(this)
        }?: pathLines.postValue(mutableListOf(mutableListOf()))

    fun addPoint(location: Location?)
    {
        location?.let {
            val pos = LatLng(location.latitude,location.longitude)
            pathLines.value?.apply {
                last().add(pos)
                pathLines.postValue(this)
            }
        }
    }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            if(isTracking.value!!)
            {
                result?.locations?.let { locations->
                    for(location in locations) {
                        addPoint(location)
                        Timber.d("new location : ${location.latitude},${location.longitude}")
                    }
                }
            }
            super.onLocationResult(result)
        }
    }

    @SuppressLint("MissingPermission")
    fun updateLocationTracking(isTracking :Boolean)
    {
        if(isTracking)
        {
            if(Utility.hasPermissions(this)){
                val request = LocationRequest().apply {
                    interval = LOCATION_UPDATE_INTERVAL
                    fastestInterval = LOCATION_FASTEST_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        }else
        {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    private fun updateNotification(isTracking: Boolean)
    {
        val pendingIntent = if(isTracking) {
            val pauseIntent = Intent(this,TrackingService::class.java).apply {
                action = ACTION_PAUSE_SERVICE
            }
            getService(this,1,pauseIntent, FLAG_UPDATE_CURRENT)
        }
        else {
            val resumeIntent = Intent(this,TrackingService::class.java).apply {
                action = ACTION_START_OR_RESUME_SERVICE
            }
            getService(this,1,resumeIntent, FLAG_UPDATE_CURRENT)
        }
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationButtonText = if(isTracking) "Pause" else "Start"

        currNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible=true
            set(currNotificationBuilder,ArrayList<NotificationCompat.Action>())
        }

        if(!serviceKilled) {
            currNotificationBuilder = baseNotificationBuilder
                .addAction(R.drawable.ic_pause_black, notificationButtonText, pendingIntent)
            notificationManager.notify(NOTIFICATION_ID, currNotificationBuilder.build())
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action){
                ACTION_START_OR_RESUME_SERVICE -> {
                    if(isFirstRun)
                    {
                        startNotificationForegroundService()
                        isFirstRun=false
                    }
                    else {
                        startTimer()
                        Timber.d("Started or resumed service")
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    pauseService()
                    Timber.d("paused service")
                }
                ACTION_STOP_SERVICE ->{
                    Timber.d("Stopped service")
                    killService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    fun pauseService()
    {
        isTracking.postValue(false)
        isTimerEnabled = false
    }

    fun startNotificationForegroundService()
    {
        startTimer()
        isTracking.postValue(true)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createNotificationChannel(notificationManager)
        startForeground(NOTIFICATION_ID,baseNotificationBuilder.build())

        timeRunInSeconds.observe(this,Observer{
            if(!serviceKilled) {
                val notification = currNotificationBuilder
                    .setContentText(Utility.formattedString(it * 1000L))
                notificationManager.notify(NOTIFICATION_ID, notification.build())
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID,NOTIFICATION_CHANNEL_NAME,NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
    }
}