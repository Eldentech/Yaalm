package com.eldentech.yaalm

import android.location.Location
import com.google.android.gms.common.api.ResolvableApiException

/**
 * Location Data
 *
 * @property locationDataStatus [LocationDataStatus] of current status
 * @property location [Location] data. Will always be null except status is [LocationDataStatus.HAS_LOCATION]
 * @property resolvable [ResolvableApiException] Will not be null if [LocationDataStatus.NEED_TO_ENABLE_LOCATION]
 */
data class LocationData internal constructor(var locationDataStatus: LocationDataStatus = LocationDataStatus.UNKNOWN, var location: Location? = null, internal var resolvable: ResolvableApiException? = null) {
    internal constructor(location: Location) : this(LocationDataStatus.HAS_LOCATION, location)
    internal constructor(resolvable: ResolvableApiException) : this(LocationDataStatus.NEED_TO_ENABLE_LOCATION, null, resolvable)
}

enum class LocationDataStatus {
    /**
     * Initial status.
     *
     * It will be never received as a LiveData value
     */
    UNKNOWN,
    /**
     * This result will be received if application has no permission.
     *
     * If you disable automatic permission requests with
     * [YaalmConfiguration.Builder.disableAutomaticPermissionRequests]
     * you should observe [Yaalm.locationPermissionLiveData] and should show custom
     * feedback actions like showing popups or descriptive information why you need user's location.
     */
    PERMISSION_REQUIRED,
    /**
     * This result will be received if Location Settings not enabled over device.
     * [LocationDataStatus] also receive internal [ResolvableApiException]
     *
     */
    NEED_TO_ENABLE_LOCATION,
    /**
     * This result will be received if Location Setting enable request is
     * rejected by user.
     */
    REJECTED_TO_ENABLE_LOCATION,
    /**
     * This result will be received when device is trying to acquire location.
     */
    WAITING_FOR_LOCATION,
    /**
     * This result will be received when we have location. Also [LocationData.location] will not be
     * null.
     */
    HAS_LOCATION
}