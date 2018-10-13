package com.eldentech.yaalm

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.annotation.MainThread
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Observer
import io.reactivex.subjects.PublishSubject
import java.lang.ref.WeakReference


/**
 * Permission manager to handle permission actions and controls.
 *
 * This manager helps us to remove all application control actions
 * from code. But it requires inside every activity to implement
 * ```Yaalm.setActiveActivity(activity: Activity)``` on Resume.
 *
 * And to acquire permission result it is needed to implement
 * ``` Yaalm.onRequestPermissionsResult(requestCode: Int, grantResults: IntArray) ```
 * inside overriden onPermissionResult method.
 *
 */
class LocationPermissionManager internal constructor(val config: YaalmConfiguration) {
    private val LOCATION_PERMISSION_REQUEST: Int = 0x001
    private var activity = WeakReference<Activity?>(null)
    private val locationPermissionStatusObservablePublish = PublishSubject.create<LocationPermissionStatus>()
    internal val locationPermissionLiveData = MutableLiveData<LocationPermissionStatus>()

    private var locationPermissionStatus = LocationPermissionStatus.UNKNOWN
        set(value) {
            locationPermissionStatusObservablePublish.onNext(value)
            locationPermissionLiveData.postValue(value)
            field = value
        }

    /**
     *  Permission status setter function when app has no permission
     *  It will set denied if user at least once see permission request
     *  @see [ContextCompat.checkSelfPermission]
     */
    private fun hasNoPermission() {
        locationPermissionStatus = if (checkDenied()) LocationPermissionStatus.PERMISSION_DENIED
        else LocationPermissionStatus.HAS_NO_PERMISSION
    }

    /**
     * Internal function to pass active activity from [Yaalm] to [LocationPermissionManager]
     */
    @MainThread
    internal fun setActiveActivity(activity: Activity) {
        this.activity = WeakReference(activity)
        if (checkPermission()) {
            hasNoPermission()
        } else {
            locationPermissionStatus = LocationPermissionStatus.PERMISSION_GRANTED
        }
        if (locationPermissionStatus.ordinal < LocationPermissionStatus.REQUESTING_PERMISSION.ordinal) {
            if (shouldShowRationale(activity)) {
                locationPermissionStatus = LocationPermissionStatus.SHOULD_SHOW_PERMISSION_RATIONALE
                if (config.showAutomaticPermissionRequests) {
                    showRequestRationale(activity)
                }
            } else {
                if (config.showAutomaticPermissionRequests) {
                    requestPermission(activity)
                }

            }
        }

    }

    /**
     * Internal callback register function for [Observer]
     */
    @SuppressLint("CheckResult")
    internal fun register(observer: LocationPermissionStatus.() -> Unit) {
        locationPermissionStatusObservablePublish.subscribe { it: LocationPermissionStatus ->
            observer.invoke(it)
        }
    }

    /**
     * Helper function to show [Snackbar] information and send intent [Settings.ACTION_APPLICATION_DETAILS_SETTINGS]
     */
    private fun showRequestRationale(activity: Activity) {
        Snackbar.make(activity.findViewById(android.R.id.content), config.snackBarPermissionMessage,
                Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok) {
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", activity.getPackageName(), null)
            intent.data = uri
            startActivity(activity, intent, null)
        }.show()
    }

    /**
     * Helper function for checking if app has permission.
     */
    private fun checkPermission() = ContextCompat.checkSelfPermission(config.context,
            config.permissionLevel) != PackageManager.PERMISSION_GRANTED

    /**
     * Helper function for checking if user denied permission.
     */
    private fun checkDenied() = ContextCompat.checkSelfPermission(config.context,
            config.permissionLevel) == PackageManager.PERMISSION_DENIED

    /**
     * Helper function for checking if we need to show [ActivityCompat.shouldShowRequestPermissionRationale]
     * for [YaalmConfiguration.permissionLevel].
     */
    private fun shouldShowRationale(activity: Activity) = ActivityCompat.shouldShowRequestPermissionRationale(activity,
            config.permissionLevel)

    /**
     * Helper function for requesting permission
     */
    private fun requestPermission(activity: Activity) {
        locationPermissionStatus = LocationPermissionStatus.REQUESTING_PERMISSION
        ActivityCompat.requestPermissions(activity, arrayOf(
                config.permissionLevel), LOCATION_PERMISSION_REQUEST)

    }

    /**
     * Internal function to receive permission result from [Yaalm]
     */
    internal fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    locationPermissionStatus = LocationPermissionStatus.PERMISSION_GRANTED
                } else {
                    locationPermissionStatus = LocationPermissionStatus.PERMISSION_DENIED
                    if (config.showAutomaticPermissionRequests) {
                        if (activity.get() != null)
                            showRequestRationale(activity.get()!!)
                    }
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }


}
