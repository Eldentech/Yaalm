package com.eldentech.yaalm

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.annotation.IntDef
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.material.snackbar.Snackbar
import java.lang.ref.WeakReference
import java.security.InvalidParameterException

/**
 * Location Manager for all your location needs.
 *
 * To use it you should implement as:
 * ```
 * class MyLocationNeededActivity : AppCompatActivity() {
 *
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *          super.onCreate(savedInstanceState)
 *          Yaalm.configure(this) //This has to be done once inside app. Can be done inside Overriden Application.
 *      }
 *
 *      override fun onResume() {
 *          super.onResume()
 *          Yaalm.instance.setActiveActivity(this)
 *      }
 *
 *      override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
 *          Yaalm.instance.onRequestPermissionsResult(requestCode,grantResults)
 *          super.onRequestPermissionsResult(requestCode, permissions, grantResults)
 *      }
 *
 *      override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
 *          Yaalm.instance.onActivityResult(requestCode,resultCode,data)
 *          super.onActivityResult(requestCode, resultCode, data)
 *      }
 * }
 * ```
 */
class Yaalm private constructor(val yaalmConfiguration: YaalmConfiguration) {
    private val REQUEST_CHECK_LOCATION_SETTINGS: Int = 0x0543


    /**
     * Companion object for [Yaalm]
     */
    companion object {
        private var INSTANCE: Yaalm? = null

        /**
         * Configures yaalm with default configurations
         *
         * For details see [YaalmConfiguration]
         */
        fun configure(context: Context) {
            val yaalmConfiguration: YaalmConfiguration = YaalmConfiguration.Builder(context).build()
            configure(yaalmConfiguration)
        }

        /**
         * Configures yaalm with given configurations
         *
         * For details see [YaalmConfiguration]
         *
         * @param yaalmConfiguration [YaalmConfiguration]
         *
         *
         */
        private fun configure(yaalmConfiguration: YaalmConfiguration) {
            if (INSTANCE == null)
                INSTANCE = Yaalm(yaalmConfiguration)
            else
                throw UnsupportedOperationException("You can configure Yaalm once inside app.")
        }

        /**
         * Instance of yaalm.
         *
         * @throws IllegalAccessException if instance called before [Yaalm] configured.
         */
        val instance: Yaalm
            get() {
                if (INSTANCE == null)
                    throw IllegalAccessException("Fist you need to configure Yaalm. Did you " +
                            "forget to call Yaalm.configure(context)?")
                return INSTANCE!!
            }
    }

    private val TAG: String = "Yaalm"
    private val locationPermissionManager = LocationPermissionManager(yaalmConfiguration)
    private val internalLiveData = LocationLiveData(yaalmConfiguration, AppExecutors())

    /**
     * Location LiveData that streams [LocationData]
     * @see [LocationData]
     */
    val locationLiveData: LiveData<LocationData> by lazy {
        Transformations.map(internalLiveData) {
            if (it.locationDataStatus == LocationDataStatus.NEED_TO_ENABLE_LOCATION || it.locationDataStatus == LocationDataStatus.REJECTED_TO_ENABLE_LOCATION) {
                if (yaalmConfiguration.showAutomaticLocationRequests) {
                    when (it.locationDataStatus) {
                        LocationDataStatus.NEED_TO_ENABLE_LOCATION -> resolve(it.resolvable)
                        LocationDataStatus.REJECTED_TO_ENABLE_LOCATION -> showEnableLocationMessage()
                        else -> { }
                    }
                }
            }
            it
        }
    }

    /**
     * Location permission LiveData that streams [LocationPermissionStatus]
     *
     * @see [LocationPermissionStatus]
     */
    val locationPermissionLiveData by lazy {
        locationPermissionManager.locationPermissionLiveData
    }

    private fun showEnableLocationMessage() {
        val activity: Activity? = this.activity.get()
        if (activity != null && yaalmConfiguration.showAutomaticLocationRequests) {
            Snackbar.make(activity.findViewById(android.R.id.content), yaalmConfiguration.snackBarLocationSettingsMessage,
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok) {
                internalLiveData.check()
            }.show()
        }
    }

    private fun resolve(resolvable: ResolvableApiException?) {
        if (activity.get() != null) {
            try {
                resolvable?.startResolutionForResult(activity.get(), REQUEST_CHECK_LOCATION_SETTINGS)
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
            }

        }
    }

    private var activity = WeakReference<Activity?>(null)

    init {
        locationPermissionManager.register {
            if (this == LocationPermissionStatus.PERMISSION_GRANTED)
                internalLiveData.check()
        }
    }

    /**
     * This function needed to be called when activity comes foreground.
     */
    @MainThread
    fun setActiveActivity(activity: Activity) {
        this.activity = WeakReference(activity)
        locationPermissionManager.setActiveActivity(activity)
    }

    /**
     * Delegate function for [Activity.onRequestPermissionsResult]
     */
    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray) {
        locationPermissionManager.onRequestPermissionsResult(requestCode, grantResults)
    }

    /**
     * Delegate function for [Activity.onActivityResult]
     */
    fun onActivityResult(requestCode: Int, resultCode: Int) {
        when (requestCode) {
            REQUEST_CHECK_LOCATION_SETTINGS -> when (resultCode) {
                Activity.RESULT_OK -> internalLiveData.check()
                Activity.RESULT_CANCELED -> internalLiveData.rejectedEnableLocation()
            }
        }
    }

    /**
     * Function to change Location Request Accuracy dynamically
     *
     * @throws [InvalidParameterException] if accuracy is not one of
     *  [LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY]
     *  ,[LocationRequest.PRIORITY_HIGH_ACCURACY]
     *  ,[LocationRequest.PRIORITY_LOW_POWER]
     *  ,[LocationRequest.PRIORITY_NO_POWER]
     */
    fun changeAccuracy(accuracy: Int) {
        if(!accuracy.isInValidList(
                         LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
                        ,LocationRequest.PRIORITY_HIGH_ACCURACY
                        ,LocationRequest.PRIORITY_LOW_POWER
                        ,LocationRequest.PRIORITY_NO_POWER
                )
        ) {
          throw InvalidParameterException("""Invalid accuracy :$accuracy
              |You can set accuracy only to:
              |LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
              |,LocationRequest.PRIORITY_HIGH_ACCURACY
              |,LocationRequest.PRIORITY_LOW_POWER
              |,LocationRequest.PRIORITY_NO_POWER""".trimMargin())
        }
        yaalmConfiguration.accuracy = accuracy
        internalLiveData.registerForUpdates()

    }

    /**
     * *Default: 60 minute*
     *
     * Sets Location Request Interval dynamically
     *
     */
    fun setUpdateInterval(interval: Long) {
        assert(!interval.isInValidRange(0,Long.MAX_VALUE)) {
            "Interval can not be lover than zero."
        }
        yaalmConfiguration.updateInteval = interval
        internalLiveData.registerForUpdates()
    }


}

