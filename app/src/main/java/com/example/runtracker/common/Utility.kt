package com.example.runtracker.common

import android.Manifest
import android.content.Context
import android.location.Location
import android.os.Build
import com.example.runtracker.runningapp.services.polyline
import pub.devrel.easypermissions.EasyPermissions
import java.util.concurrent.TimeUnit

object Utility {

    fun hasPermissions(context: Context) =
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
        {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
                )
        }
        else
        {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }

    fun formattedString(ms:Long,milliReqd:Boolean = false):String{
        var millisec = ms
        val hours = TimeUnit.MILLISECONDS.toHours(millisec)
        millisec-= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millisec)
        millisec-= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millisec)
        millisec-= TimeUnit.SECONDS.toMillis(seconds)
        var s = "${if(hours<10) "0" else ""}${hours}:${if(minutes<10) "0" else ""}${minutes}:${if(seconds<10) "0" else ""}${seconds}"
        if(milliReqd)
        {
            millisec/=10
            s+=":${if(millisec<10) "0" else ""}$millisec"
        }
        return s
    }

    fun lengthOfPolyline(path: polyline):Int{
        var distance = 0f
        for(i in 0..path.size-2)
        {
            val pos1 = path[i]
            val pos2 = path[i+1]
            val result = FloatArray(1)
            Location.distanceBetween(pos1.latitude,pos1.longitude,pos2.latitude, pos2.longitude, result)
            distance+=result[0]
        }
        return distance.toInt()
    }
}