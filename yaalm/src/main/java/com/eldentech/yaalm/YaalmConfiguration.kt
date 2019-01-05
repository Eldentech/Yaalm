package com.eldentech.yaalm

import android.Manifest
import android.content.Context
import androidx.annotation.StringRes
import com.google.android.gms.location.LocationRequest
import java.security.InvalidParameterException

/**
 * This class provides custom configuration for [Yaalm]
 */
class YaalmConfiguration private constructor(val context: Context) {

    /**
     * *Default: true*
     *
     * If this is set to false Yaalm will not handle
     * permission requests. User will need to handle request for permissions.
     */
    var showAutomaticPermissionRequests = true
        private set
    /**
     * *Default: true*
     *
     * If this is set to false Yaalm will not handle
     * requests to enable location requests. User will need to enable location.
     */
    var showAutomaticLocationRequests = true
        private set
    /**
     * *Default: We need your location permission to provide you accurate results.*
     *
     * This is the message content as a SnackBar message if user disable location permission.
     */
    var snackBarPermissionMessage: String = context.getString(R.string.we_need_your_location_permission)
        private set
    /**
     * *Default: You need to enable location to for accurate results.*
     *
     * This is the message content as a SnackBar message if user disable location permission.
     */
    var snackBarLocationSettingsMessage: String = context.getString(R.string.we_need_your_location_settings)
        private set
    /**
     * *Default: [Manifest.permission.ACCESS_FINE_LOCATION]*
     *
     * Permission Level requested for app.
     */
    var permissionLevel: String = Manifest.permission.ACCESS_FINE_LOCATION
        private set
    /**
     * *Default: [LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY]*
     *
     * Location request accuracy
     * @see LocationRequest
     */
    internal var accuracy: Int = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

    /**
     * *Default: 60 seconds*
     *
     * Location update interval in milliseconds
     * @see LocationRequest
     */
    internal var updateInterval: Long = 60 * 1000

    /**
     * Builder class for [YaalmConfiguration]
     * @param context [Context] of activity service or application. Even it is taking
     * context initialization acquire application context. That we don't want take any other
     * context like activity context as reference.
     */
    class Builder(private val context: Context) {
        private val yaalmConfiguration = YaalmConfiguration(context.applicationContext)

        /**
         * Disables automatic permission requests.
         *
         * @see [YaalmConfiguration.showAutomaticPermissionRequests]
         */
        fun disableAutomaticPermissionRequests(): Builder {
            yaalmConfiguration.showAutomaticPermissionRequests = false
            return this
        }

        /**
         * Disables automatic location requests.
         *
         * @see [YaalmConfiguration.showAutomaticLocationRequests]
         */
        fun disableAutomaticLocationRequests(): Builder {
            yaalmConfiguration.showAutomaticLocationRequests = false
            return this
        }

        /**
         * Sets the SnackBar message as content.
         * @param message String content message
         * @see [YaalmConfiguration.snackBarPermissionMessage]
         */
        fun setYaalmSnackbarPermissionMessage(message: String): Builder {
            yaalmConfiguration.snackBarPermissionMessage = message
            return this
        }

        /**
         * Sets the SnackBar message as content.
         * @param message [StringRes] of message
         * @see [YaalmConfiguration.snackBarPermissionMessage]
         */
        fun setYaalmSnackbarPermissionMessage(@StringRes message: Int): Builder {
            yaalmConfiguration.snackBarPermissionMessage = this.context.getString(message)
            return this
        }

        /**
         * Sets the SnackBar message as content to enable location.
         * @param message String content message
         * @see [YaalmConfiguration.snackBarLocationSettingsMessage]
         */
        fun setYaalmSnackbarLocationSettingsMessage(message: String): Builder {
            yaalmConfiguration.snackBarLocationSettingsMessage = message
            return this
        }

        /**
         * Sets the SnackBar message as content to enable location.
         * @param message [StringRes] of message
         * @see [YaalmConfiguration.snackBarLocationSettingsMessage]
         */
        fun setYaalmSnackbarLocationSettingsMessage(@StringRes message: Int): Builder {
            yaalmConfiguration.snackBarLocationSettingsMessage = this.context.getString(message)
            return this
        }

        /**
         * Sets the permission level for app.
         * @param permissionlevel of [Manifest.permission.ACCESS_FINE_LOCATION] or [Manifest.permission.ACCESS_COARSE_LOCATION]
         * @see [YaalmConfiguration.snackBarLocationSettingsMessage]
         * @throws [InvalidParameterException] if permissionLevel not equal android.permission.ACCESS_COARSE_LOCATION or android.permission.ACCESS_FINE_LOCATION
         */
        fun setsPermissionLevel(permissionLevel: String): Builder {
            if(permissionLevel.isInValidList(Manifest.permission.ACCESS_COARSE_LOCATION,
                                             Manifest.permission.ACCESS_FINE_LOCATION)) {
                yaalmConfiguration.permissionLevel = permissionLevel
            } else {
                throw InvalidParameterException("Permission can be only android.permission.ACCESS_COARSE_LOCATION " +
                        "or android.permission.ACCESS_FINE_LOCATION")
            }
            return this
        }

        /**
         * *Default: [LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY]*
         *
         * Sets Location Request Accuracy accuracy
         * @param accuracy Accuracy for location.
         * @see [LocationRequest]
         *
         */
        fun setAccuracy(accuracy: Int): Builder {
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
            return this
        }

        /**
         * *Default: 60 minute*
         *
         * Sets Location Request Interval in milliseconds
         *
         */
        fun setUpdateInterval(interval: Long): Builder {
            assert(!interval.isInValidRange(0,Long.MAX_VALUE)) {
                "Interval can not be lover than zero."
            }
            yaalmConfiguration.updateInterval = interval
            return this
        }

        /**
         * Build function for our builder.
         */
        fun build(): YaalmConfiguration {
            return this.yaalmConfiguration
        }

    }

}