package com.eldentech.yaalm


import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.location.LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.sameInstance
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.security.InvalidParameterException


@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class YaalmConfigurationTest {

    lateinit var app : Application

    @Rule
    @JvmField
    var exceptionRule = ExpectedException.none()

    @Before
    fun setUp() {
        app = ApplicationProvider.getApplicationContext()
    }

    @After
    fun tearDown() {
        app.onTerminate()
    }

    @Test
    fun `test default configs`() {
        val config = YaalmConfiguration.Builder(app).build()
        assertThat(config.snackBarLocationSettingsMessage, equalTo("You need to enable location to for accurate results."))
        assertThat(config.snackBarPermissionMessage, equalTo("We need your location permission to provide you accurate results."))
        assertThat(config.accuracy, equalTo(PRIORITY_BALANCED_POWER_ACCURACY))
        assertThat(config.context, sameInstance(app.applicationContext))
        assertThat(config.permissionLevel, equalTo(ACCESS_FINE_LOCATION))
        assertThat(config.showAutomaticLocationRequests, equalTo(true))
        assertThat(config.showAutomaticPermissionRequests, equalTo(true))
        assertThat(config.updateInterval, equalTo(60L*1000L))
    }
    @Test
    fun `test snackbar messages`() {
        val testString = YaalmConfiguration.Builder(app)
                .setYaalmSnackbarLocationSettingsMessage("test-message-location")
                .setYaalmSnackbarPermissionMessage("test-message-permission")
                .build()
        assertThat(testString.snackBarLocationSettingsMessage, equalTo("test-message-location"))
        assertThat(testString.snackBarPermissionMessage, equalTo("test-message-permission"))

        val testResource = YaalmConfiguration.Builder(app)
                .setYaalmSnackbarLocationSettingsMessage(R.string.abc_search_hint)
                .setYaalmSnackbarPermissionMessage(R.string.abc_searchview_description_search)
                .build()
        assertThat(testResource.snackBarLocationSettingsMessage, equalTo("Search…"))
        assertThat(testResource.snackBarPermissionMessage, equalTo("Search"))

    }
    @Test
    fun `test custom config`() {
        val customConfig = YaalmConfiguration.Builder(app)
                .disableAutomaticLocationRequests()
                .disableAutomaticPermissionRequests()
                .setAccuracy(PRIORITY_HIGH_ACCURACY)
                .setUpdateInterval(1000)
                .setsPermissionLevel(ACCESS_COARSE_LOCATION)
                .build()
        assertThat(customConfig.updateInterval, equalTo(1000L))
        assertThat(customConfig.showAutomaticPermissionRequests, equalTo(false))
        assertThat(customConfig.showAutomaticLocationRequests, equalTo(false))
        assertThat(customConfig.accuracy, equalTo(PRIORITY_HIGH_ACCURACY))
        assertThat(customConfig.permissionLevel, equalTo(ACCESS_COARSE_LOCATION))

    }

    @Test
    fun `test invalid accuracy`() {
        val accuracy = 980980
        exceptionRule.expect(InvalidParameterException::class.java)
        exceptionRule.expectMessage("""Invalid accuracy :$accuracy
              |You can set accuracy only to:
              |LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
              |,LocationRequest.PRIORITY_HIGH_ACCURACY
              |,LocationRequest.PRIORITY_LOW_POWER
              |,LocationRequest.PRIORITY_NO_POWER""".trimMargin())
        YaalmConfiguration.Builder(app).setAccuracy(accuracy)


    }

    @Test
    fun `test invalid permission level`() {
        val test = "test-level"
        exceptionRule.expect(InvalidParameterException::class.java)
        exceptionRule.expectMessage("Permission can be only android.permission.ACCESS_COARSE_LOCATION " +
                "or android.permission.ACCESS_FINE_LOCATION")
        YaalmConfiguration.Builder(app).setsPermissionLevel(test)
    }

    @Test
    fun `test invalid update interval`() {
        exceptionRule.expect(InvalidParameterException::class.java)
        exceptionRule.expectMessage("Interval can not be lover than zero or higher than ${Long.MAX_VALUE - 1}}.")
        YaalmConfiguration.Builder(app).setUpdateInterval(-150)
    }

}