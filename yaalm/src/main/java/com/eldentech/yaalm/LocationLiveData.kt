package com.eldentech.yaalm

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener

/**
 * Location Live Data provides [LocationData] as a LiveData
 *
 */
internal class LocationLiveData(private val configuration: YaalmConfiguration, private val executors: AppExecutors) : LiveData<LocationData>() {

    private var fusedLocationProviderClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(configuration.context)
    private var settingsClient: SettingsClient = LocationServices.getSettingsClient(configuration.context)
    private val TAG = "YaalmLocationLiveData"

    /**
     * We check for status when LiveData becomes active.
     *
     * @see [LiveData.onActive]
     */
    override fun onActive() {
        super.onActive()
        check()
    }

    /**
     * Internal check function to get status of our location
     */
    internal fun check() {
        if (hasActiveObservers()) {
            if (checkPermission()) {
                val builder = LocationSettingsRequest.Builder().addLocationRequest(LocationRequest.create())
                val task = settingsClient.checkLocationSettings(builder.build())
                task.addOnSuccessListener(executors.mainThread(), settingsSuccessListener)
                task.addOnFailureListener(executors.mainThread(), settingsFailureListener)
            } else {
                value = LocationData(LocationDataStatus.PERMISSION_REQUIRED)
            }
        }
    }

    /**
     * We remove location updates when our LiveData becomes inactive.
     *
     * @see [LiveData.onInactive]
     * @see [FusedLocationProviderClient.removeLocationUpdates]
     */
    override fun onInactive() {
        if(BuildConfig.DEBUG) { Log.d(TAG, "Removed location callbacks") }
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        super.onInactive()
    }

    /**
     * Location callback to register when LiveData is active.
     */
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            if(BuildConfig.DEBUG)
            {
                Log.d(TAG, "Received Location: ${locationResult.lastLocation}")
            }
            value = LocationData(locationResult.lastLocation)
        }
    }

    /**
     * Settings success listener when we successfully received our location settings
     * @see [LocationSettingsResponse]
     * @see [SettingsClient]
     */
    private val settingsSuccessListener = OnSuccessListener<LocationSettingsResponse> { _ ->
        registerForUpdates()
    }

    @SuppressLint("MissingPermission")
    internal fun registerForUpdates() {
        if(hasActiveObservers()) {
            if(checkPermission()){
                val listener = OnSuccessListener<Location> {
                    if (hasActiveObservers()) {
                        value = if (it != null) {
                            LocationData(it)
                        } else {
                            LocationData(LocationDataStatus.WAITING_FOR_LOCATION)
                        }
                    }
                }
                fusedLocationProviderClient.lastLocation.addOnSuccessListener(executors.mainThread(), listener)
                val locationRequest = LocationRequest.create()
                locationRequest.priority = configuration.accuracy
                locationRequest.interval = configuration.updateInterval
                val looper = Looper.getMainLooper()
                fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                val task = fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, looper)
                if(!task.isSuccessful) {
                    Log.e(TAG, "Cannot request location updates:",task.exception)
                }
            } else {
                value = LocationData(LocationDataStatus.PERMISSION_REQUIRED)
            }
        }
    }

    /**
     * Settings failure listener when we successfully received our location settings
     * @see [SettingsClient]
     * @see [ApiException]
     */
    private val settingsFailureListener = OnFailureListener { e ->
        val statusCode = (e as ApiException).statusCode
        when (statusCode) {
            CommonStatusCodes.RESOLUTION_REQUIRED -> {
                if (hasActiveObservers()) {
                    value = LocationData(e as ResolvableApiException)
                }
            }
            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                value = LocationData(LocationDataStatus.REJECTED_TO_ENABLE_LOCATION)
            }
        }
    }

    /**
     * Helper function for checking permission
     */
    private fun checkPermission() = ContextCompat.checkSelfPermission(configuration.context,
            configuration.permissionLevel) == PackageManager.PERMISSION_GRANTED

    /**
     * Internal function to receive rejected status of permission.
     */
    internal fun rejectedEnableLocation() {
        if (hasActiveObservers())
            value = LocationData(LocationDataStatus.REJECTED_TO_ENABLE_LOCATION)
    }
}