package com.example.flappybird.UI

import android.support.v7.app.AppCompatActivity
import android.os.Bundle


class PlayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val playView = PlayView(this)
        setContentView(playView)
    }
}
