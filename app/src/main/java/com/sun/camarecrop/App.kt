package com.sun.camarecrop

import android.app.Application

/**
 * Created by nguyenxuanhoi on 2019-09-06.
 * @author nguyen.xuan.hoi@sun-asterisk.com
 */
class App : Application() {
    companion object {
        lateinit var INSTANCE: Application
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }
}