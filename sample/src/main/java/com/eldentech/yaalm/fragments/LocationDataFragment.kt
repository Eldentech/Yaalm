package com.eldentech.yaalm.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.eldentech.yaalm.LocationData
import com.eldentech.yaalm.Yaalm
import com.eldentech.yaalm.R
import kotlinx.android.synthetic.main.fragment_location_data.*

class LocationDataFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_location_data, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Yaalm.instance.locationLiveData.observe(this, Observer<LocationData> {
            status.text = it?.locationDataStatus?.name
            if(it!= null){
                val locationString = """
                    Latitude: ${it.location?.latitude}
                    Longitude: ${it.location?.longitude}
                    Altitude: ${it.location?.altitude}
                    """.trimIndent()
                location.text = locationString
            }

        })

    }

    override fun onResume() {
        super.onResume()
        Yaalm.instance.setActiveActivity(this.activity as Activity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Yaalm.instance.onActivityResult(requestCode,resultCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Yaalm.instance.onRequestPermissionsResult(requestCode,grantResults)
    }

}
