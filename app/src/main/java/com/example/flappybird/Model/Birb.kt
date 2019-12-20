package com.example.flappybird.Model

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.flappybird.R

class Birb (res : Resources) {
    var x : Int = 0
        get() = field
        set(value) {
            field = value
        }
    var y : Int = 0
        get() = field
        set(value) {
            field = value
        }
    val maxFrame : Int = 7
    var currentFrame : Int = 0
        get() = field
        set(value) {
            field = value
        }
    var birbList : ArrayList<Bitmap>

    init {
        birbList = arrayListOf()
        birbList.add(BitmapFactory.decodeResource(res, R.drawable.frame_0))
        birbList.add(BitmapFactory.decodeResource(res, R.drawable.frame_1))
        birbList.add(BitmapFactory.decodeResource(res, R.drawable.frame_2))
        birbList.add(BitmapFactory.decodeResource(res, R.drawable.frame_3))
        birbList.add(BitmapFactory.decodeResource(res, R.drawable.frame_4))
        birbList.add(BitmapFactory.decodeResource(res, R.drawable.frame_5))
        birbList.add(BitmapFactory.decodeResource(res, R.drawable.frame_6))
        birbList.add(BitmapFactory.decodeResource(res, R.drawable.frame_7))

        x = ScreenSize.SCREEN_WIDTH/2 - birbList[0].width/2
        y = ScreenSize.SCREEN_WIDTH/2 - birbList[0].width/2
    }

    fun getBirb(current : Int) : Bitmap{
        return birbList.get(current)
    }
}