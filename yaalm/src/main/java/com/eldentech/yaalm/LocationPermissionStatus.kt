package com.eldentech.yaalm

enum class LocationPermissionStatus {
    /**
     * Initial status will not be received.
     */
    UNKNOWN,
    /**
     * Current permission status is denied from user.
     *
     * This means we need to show a descriptive
     * explanation about why we need
     * to get user's location
     */
    PERMISSION_DENIED,
    /**
     * Current permission status is denied from user.
     *
     * This means we need to show a descriptive
     * explanation about why we need
     * to get user's location
     */
    SHOULD_SHOW_PERMISSION_RATIONALE,
    /**
     * Current permission not yet requested from user.
     *
     * This means we just need to request for permission
     */
    HAS_NO_PERMISSION,
    /**
     * Requesting the permission
     *
     * This means we showed permission dialog but it is not yet received
     * from user.
     */
    REQUESTING_PERMISSION,
    /**
     * User granted permission and we don't need to do anything.
     * We can track [LocationDataStatus] to get location.
     */
    PERMISSION_GRANTED

}