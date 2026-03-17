package com.techyexamplelogin.adminwaveoffood

import android.app.Application
import com.cloudinary.android.MediaManager

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val config = mapOf(
            "cloud_name" to "dm0iywe3o"
        )

        MediaManager.init(this, config)
    }
}
