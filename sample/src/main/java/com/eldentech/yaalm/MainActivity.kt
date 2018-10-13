package com.eldentech.yaalm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.eldentech.yaalm.fragments.LocationDataFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.root_fragment_container,LocationDataFragment())
                    .commit()
        }
    }
}
