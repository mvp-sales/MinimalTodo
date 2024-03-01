package com.example.avjindersinghsekhon.minimaltodo.analytics

import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import com.example.avjindersinghsekhon.minimaltodo.R
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.HitBuilders.EventBuilder
import com.google.android.gms.analytics.HitBuilders.ScreenViewBuilder
import com.google.android.gms.analytics.Tracker

@SuppressLint("VisibleForTests")
class AnalyticsApplication : Application() {
    private lateinit var tracker: Tracker

    override fun onCreate() {
        super.onCreate()
        val analytics = GoogleAnalytics.getInstance(this)

        /*R.xml.app_tracker contains my Analytics code
    To use this, go to Google Analytics, and get
    your code, create a file under res/xml , and save
    your code as <string name="ga_trackingId">UX-XXXXXXXX-Y</string>
    */

        tracker = analytics.newTracker(R.xml.global_tracker)
        tracker.setAppName("Minimal")
        tracker.enableExceptionReporting(true)
        try {
            tracker.setAppId(packageManager.getPackageInfo(packageName, 0).versionName)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    fun send(screenName: String) {
        send(screenName, ScreenViewBuilder().build())
    }

    private fun send(screenName: String, params: Map<String, String>) {
        if (IS_ENABLED) {
            tracker.setScreenName(screenName)
            tracker.send(params)
        }
    }

    fun send(screenName: String, category: String?, action: String?) {
        send(screenName, EventBuilder().setCategory(category!!).setAction(action!!).build())
    }

    fun send(screenName: String, category: String?, action: String?, label: String?) {
        send(screenName, EventBuilder().setCategory(category!!).setAction(action!!).setLabel(label!!).build())
    }

    companion object {
        private const val IS_ENABLED = true
    }
}
