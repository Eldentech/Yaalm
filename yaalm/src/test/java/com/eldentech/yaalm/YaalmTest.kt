package com.eldentech.yaalm

import android.app.Activity
import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.natpryce.hamkrest.equalTo
import io.mockk.spyk
import io.mockk.verify
import junit.framework.Assert.assertNotNull
import org.hamcrest.MatcherAssert.*
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class YaalmTest {

    val applicationContext = ApplicationProvider.getApplicationContext<Application>()
    lateinit var activity: Activity
    lateinit var spyInst: Yaalm

    @Before
    fun setUp() {
        activity = Robolectric.buildActivity(Activity::class.java)
                .create()
                .start()
                .get()

        Yaalm.configure(activity)
        spyInst = spyk(Yaalm.instance)
    }
    @After
    fun tearDown() {
        Yaalm.clear()
        activity.finish()
    }

    @Test
    fun getLocationLiveData() {
        assertNotNull(Yaalm.instance.locationLiveData)
    }

    @Test
    fun getLocationPermissionLiveData() {
        assertNotNull(Yaalm.instance.locationPermissionLiveData)
    }

    @Test
    fun setActiveActivity() {
        spyInst.setActiveActivity(activity)
        verify { spyInst.setActiveActivity(activity) }

    }

    @Test
    fun onRequestPermissionsResult() {
    }

    @Test
    fun onActivityResult() {
    }

    @Test
    fun changeAccuracy() {
    }

    @Test
    fun setUpdateInterval() {
    }

    @Test
    fun getYaalmConfiguration() {
    }
}