package com.example.runtracker.runningapp.ui.fragments


import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.runtracker.R
import com.example.runtracker.common.Constants.ACTION_PAUSE_SERVICE
import com.example.runtracker.common.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.runtracker.common.Constants.ACTION_STOP_SERVICE
import com.example.runtracker.common.Constants.LOCATION_PERMISSION_REQUEST_CODE
import com.example.runtracker.common.Constants.MAP_ZOOM
import com.example.runtracker.common.Constants.TRACK_COLOR
import com.example.runtracker.common.Constants.TRACK_WIDTH
import com.example.runtracker.common.Utility
import com.example.runtracker.common.Utility.formattedString
import com.example.runtracker.common.Utility.hasPermissions
import com.example.runtracker.runningapp.database.Run
import com.example.runtracker.runningapp.viewmodels.MainViewModel
import com.example.runtracker.runningapp.services.TrackingService
import com.example.runtracker.runningapp.services.polyline
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.valdesekamdem.library.mdtoast.MDToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_tracking.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.*
import javax.inject.Inject
import kotlin.math.round

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking),EasyPermissions.PermissionCallbacks {

    private val viewModel: MainViewModel by viewModels()
    private var map:GoogleMap? =null

    private var isTracking:Boolean=false
    private var pathPoints= mutableListOf<polyline>()

    @set:Inject
    var weight = 80f

    private var currentTimeMillis = 0L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mvMap.onCreate(savedInstanceState)
        mvMap.getMapAsync {
            map=it
            addAllPolylines()
        }
        createObservers()
        btToggleRun.setOnClickListener {
            toggleRun()
            btFinishRun.visibility = View.VISIBLE
        }
        btFinishRun.setOnClickListener {
            if(pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
                takeMapImage()
                saveRunInDb()
                btFinishRun.visibility = View.GONE
            }
            else{
                /** NO-OP **/
                stopRun()
                btFinishRun.visibility = View.GONE
                MDToast.makeText(requireContext(), "Unable to fetch location!!\n Run not added to database.", Toast.LENGTH_SHORT,MDToast.TYPE_ERROR).show()
                tvTimer.text = "00:00:00:00"
            }
        }
        requestPermissions()
    }

    private fun sendCommandToService(action: String){
        Intent(requireContext(),TrackingService::class.java).also {
            it.action=action
            requireContext().startService(it)
        }
    }

    private fun createObservers()
    {
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })
        TrackingService.pathLines.observe(viewLifecycleOwner, Observer {
            pathPoints = it
            addLatestLatLng()
            moveCameraToUser()
        })

        TrackingService.timeRunInMillis.observe(viewLifecycleOwner, Observer {
            currentTimeMillis = it
            val formattedTime = formattedString(currentTimeMillis,true)
            tvTimer.text = formattedTime
        })
    }

    private fun toggleRun()
    {
        if(isTracking)
            sendCommandToService(ACTION_PAUSE_SERVICE)
        else
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
    }

    private fun stopRun()
    {
        sendCommandToService(ACTION_STOP_SERVICE)
    }

    private fun updateTracking(isTracking: Boolean)
    {
        this.isTracking = isTracking
        if(!isTracking)
        {
            btToggleRun.text = "Start"
        }
        else
        {
            btToggleRun.text = "Pause"
        }
    }

    private fun moveCameraToUser()
    {
        if(pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty())
        {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }

    private fun takeMapImage()
    {
        val bound = LatLngBounds.builder()
        for(polyline in pathPoints)
        {
            for(point in polyline)
            {
                bound.include(point)
            }
        }
        map?.moveCamera(CameraUpdateFactory.newLatLngBounds(
            bound.build(),
            mvMap.width,
            mvMap.height,
            ((mvMap.height)*0.05f).toInt()
        ))
    }

    fun saveRunInDb()
    {
        map?.snapshot { bmp->
            var distanceInMeters = 0
            for(polyline in pathPoints)
                distanceInMeters+=Utility.lengthOfPolyline(polyline)
            val avgSpeed = round(((distanceInMeters/1000f)/(currentTimeMillis/(1000f*60*60)))*10)/10f
            val datetimestamp = Calendar.getInstance().timeInMillis
            val caloriesBurnt = ((distanceInMeters/1000f)*weight).toInt()
            val run = Run(bmp,datetimestamp,avgSpeed,distanceInMeters,currentTimeMillis,caloriesBurnt)
            viewModel.insert(run)
            MDToast.makeText(requireContext(),"Run saved in Database",MDToast.LENGTH_LONG,MDToast.TYPE_SUCCESS).show()
            stopRun()
        }
    }

    private fun addAllPolylines()
    {
        for(polylines in pathPoints)
        {
            val polylineOptions = PolylineOptions()
                .color(TRACK_COLOR)
                .width(TRACK_WIDTH)
                .addAll(polylines)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun addLatestLatLng()
    {
        if(pathPoints.isNotEmpty() && pathPoints.last().size > 1 )
        {
            val preLastPoint = pathPoints.last()[pathPoints.last().size -2]
            val lastPoint = pathPoints.last().last()
            val polylineOptions = PolylineOptions()
                .color(TRACK_COLOR)
                .width(TRACK_WIDTH)
                .add(preLastPoint)
                .add(lastPoint)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun requestPermissions()
    {
        if(!hasPermissions(requireContext()))
        {
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                EasyPermissions.requestPermissions(
                    this,
                    "You need to access location permissions to use this app",
                    LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }else{
                EasyPermissions.requestPermissions(
                    this,
                    "You need to access location permissions to use this app",
                    LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this,perms))
        {
            AppSettingsDialog.Builder(this).build().show()
        }else
        {
            requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onLowMemory() {
        super.onLowMemory()
        mvMap?.onLowMemory()
    }

    override fun onStart() {
        super.onStart()
        mvMap?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mvMap?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mvMap?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mvMap?.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mvMap?.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mvMap?.onSaveInstanceState(outState)
    }
}