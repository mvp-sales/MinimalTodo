package com.example.avjindersinghsekhon.minimaltodo.analytics

import android.annotation.SuppressLint
import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.room.Room
import androidx.work.Configuration
import com.example.avjindersinghsekhon.minimaltodo.database.AppDatabase
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

@SuppressLint("VisibleForTests")
@HiltAndroidApp
class AnalyticsApplication : Application(), Configuration.Provider {
    //private lateinit var tracker: Tracker
    //@Inject lateinit var workerFactory: HiltWorkerFactory
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface HiltWorkerFactoryEntryPoint {
        fun workerFactory(): HiltWorkerFactory
    }


    override val workManagerConfiguration =
            Configuration.Builder()
                    .setWorkerFactory(EntryPoints.get(this, HiltWorkerFactoryEntryPoint::class.java).workerFactory())
                    .build()

    override fun onCreate() {
        super.onCreate()
        /*val analytics = GoogleAnalytics.getInstance(this)

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
        }*/
    }

    fun send(screenName: String) {
        //send(screenName, ScreenViewBuilder().build())
    }

    private fun send(screenName: String, params: Map<String, String>) {
        /*if (IS_ENABLED) {
            tracker.setScreenName(screenName)
            tracker.send(params)
        }
         */
    }

    fun send(screenName: String, category: String?, action: String?) {
        //send(screenName, EventBuilder().setCategory(category!!).setAction(action!!).build())
    }

    fun send(screenName: String, category: String?, action: String?, label: String?) {
        //send(screenName, EventBuilder().setCategory(category!!).setAction(action!!).setLabel(label!!).build())
    }

    companion object {
        private const val IS_ENABLED = true
    }
}
