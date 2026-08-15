package com.agon.app

import android.app.Application

class SalonApp : Application() {
    lateinit var repository: com.agon.app.data.SalonRepository
        private set
    lateinit var prefs: com.agon.app.data.Prefs
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        repository = com.agon.app.data.SalonRepository(this)
        prefs = com.agon.app.data.Prefs(this)
        repository.startNetwork()
    }

    companion object {
        lateinit var instance: SalonApp
            private set
    }
}
