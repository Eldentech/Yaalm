package com.eldentech.yaalm

import android.Manifest
import androidx.multidex.MultiDexApplication
import com.google.android.gms.location.LocationRequest

class App: MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        configureCustom()
    }

    /**
     * Configures yaalm with custom configuration.
     *
     * If you want to use default configuration use
     * ```kotlin
     * Yaalm.configure(context)
     * ```
     */
    private fun configureCustom() {
       val configuration =  YaalmConfiguration.Builder(this)
                .setUpdateInterval(3000)
                .setAccuracy(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setsPermissionLevel(Manifest.permission.ACCESS_FINE_LOCATION)
                .build()
        Yaalm.configure(configuration)

    }
}