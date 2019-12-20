package com.example.flappybird.Model

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager


class ScreenSize {
    companion object{
        var SCREEN_WIDTH : Int = 0
                get() = field
        var SCREEN_HEIGHT : Int = 0
                get() = field

        fun getScreenSize(context: Context) {
            val vm : WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = vm.defaultDisplay
            val metric : DisplayMetrics = DisplayMetrics()
            display.getMetrics(metric)
            SCREEN_WIDTH = metric.widthPixels
            SCREEN_HEIGHT = metric.heightPixels
        }
    }
}