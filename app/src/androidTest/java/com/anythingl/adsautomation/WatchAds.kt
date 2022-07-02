package com.anythingl.adsautomation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiScrollable
import androidx.test.uiautomator.UiSelector
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class WatchAds {
    private val tag: String = "WatchAds"
    private lateinit var appContext: Context
    private lateinit var uiDevice: UiDevice

    @Before
    fun setup() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        appContext = instrumentation.targetContext
        uiDevice = UiDevice.getInstance(instrumentation)
    }
    @Test
    fun useAppContext() {
        val video = getVideoAds()
        openYoutubeApp(video)
        watchVideo(video)
    }

    private fun getVideoAds() : Video {
        val finished = AtomicBoolean(false)
        val video = AtomicReference<Video>()
        AppClient.getInstance().appService.getVideoAds().enqueue(object : Callback<Video> {
            override fun onResponse(call: Call<Video>, response: Response<Video>) {
                video.set(response.body())
                finished.set(true)
            }

            override fun onFailure(call: Call<Video>, t: Throwable) {
                finished.set(true)
            }
        })
        while (!finished.get()) {
            // Wait for finishing
        }
        return video.get()
    }

    private fun openYoutubeApp(video: Video) {
        val videoId = video.videoId
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://$videoId"))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        appContext.startActivity(intent)
        uiDevice.waitForIdle()
    }

    private fun watchVideo(video: Video) {
        val durationNeedToWatch = 70 // hardcode
        val durationInterval = 1000 // 1 second
        val timeToClickAd = video.timeClick
        var countClicked = 0
        for (i in 0..durationNeedToWatch) {
            this.waitFor(durationInterval)
            if (i >= 70 && countClicked == 0) {
                // Skip to next video
                break
            }
            if (i == timeToClickAd) {
                // Click add
                val clicked = clickAd()
                if (clicked) {
                    countClicked += 1
                }
            }
        }
    }

    private fun clickAd(): Boolean {
        return tryClick(
            this::clickInstallationAdIfExists
        )
    }

    private fun tryClick(vararg functions: () -> Boolean) : Boolean {
        for (function in functions) {
            val clicked = function.invoke()
            if (clicked) {
                return true
            }
        }
        return false
    }

    private fun clickInstallationAdIfExists() : Boolean {
        val installButton = findView(UiSelector().description("INSTALL")) ?: return false
        installButton.clickAndWaitForNewWindow()
        // Click INSTALL button after a popup is shown
        val selector = UiSelector().text("Install")
            .packageName("com.android.vending")
            .className("android.widget.Button")
        val installButtonOnPopup = uiDevice.findObject(selector)
        if (installButtonOnPopup.exists()) {
            installButtonOnPopup.clickAndWaitForNewWindow()
            return true
        }
        return false
    }

    fun verifyAppInstalled() {

    }

    private fun findView(uiSelector: UiSelector) : UiObject? {
        val uiObject = uiDevice.findObject(uiSelector)
        if (uiObject.exists()) {
            uiObject.clickAndWaitForNewWindow()
            Log.d(tag, "findView ${uiSelector}: without scrolling")
            return uiObject
        } else {
            val uiScrollable = UiScrollable(UiSelector().scrollable(true))
            val exists = uiScrollable.scrollIntoView(uiObject)
            if (exists) {
                Log.d(tag, "clickInstallationAdIfExists: within scrolling")
                return uiObject
            }
        }
        return null
    }

    private fun waitFor(ms: Int) {
        val start = System.currentTimeMillis();
        while (true) {
            val end = System.currentTimeMillis()
            if (end - start >= ms) {
                break
            }
        }
    }
}