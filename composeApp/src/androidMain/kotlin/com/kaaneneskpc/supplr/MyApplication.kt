package com.kaaneneskpc.supplr

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.initialize

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(this)
    }
}