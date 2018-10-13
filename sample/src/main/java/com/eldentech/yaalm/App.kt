package com.eldentech.yaalm

import androidx.multidex.MultiDexApplication

class App: MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        Yaalm.configure(this)
    }
}