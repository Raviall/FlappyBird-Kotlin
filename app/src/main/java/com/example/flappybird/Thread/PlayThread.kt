package com.example.flappybird.Thread

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.Log
import android.view.SurfaceHolder
import com.example.flappybird.Model.BackgroundImage
import com.example.flappybird.Model.Birb
import com.example.flappybird.Model.ScreenSize
import com.example.flappybird.R

class PlayThread : Thread{

    private val TAG : String = "PlayThread"
    private var holder : SurfaceHolder
    private var resources : Resources

    private val FPS : Int = (1000.0/60.0).toInt() //Time passed per frame. 60 FPS
    private val backgroundImage = BackgroundImage() //Hello, new object
    private var startTime : Long = 0
    private var frameTime : Long = 0
    private val velocity = 3
    private val birb : Birb //birb model that will fly


    var isRunning : Boolean = false //Flag to Run or Stop
        get() = field
        set(value) {
            field = value
        }


    constructor(holder: SurfaceHolder, resources: Resources) {
        this.holder = holder
        this.resources = resources
        isRunning = true
        birb = Birb(resources)
    }

    override fun run() {
        Log.d(TAG, "Thread Started")
        while (isRunning){
            startTime = System.nanoTime()
            val canvas = holder.lockCanvas()
            if(canvas != null){
                try{
                    synchronized(holder){
                        render(canvas)
                        renderBirb(canvas)
                    }
                }
                finally{
                    holder.unlockCanvasAndPost(canvas)
                }
            }
            frameTime = (System.nanoTime() - startTime) / 1000000
            if (frameTime < FPS) {
                try{
                    Thread.sleep( FPS - frameTime)
                }catch (e : InterruptedException){
                    Log.e("Interrupted Stuff", "Thread is asleep. Error.")
                }
            }
        }
        Log.d(TAG, "Thread has reached its finale. Zargothrax Approves.")
    }

    private fun renderBirb(canvas: Canvas?) {
        var current : Int = birb.currentFrame
        canvas!!.drawBitmap(birb.getBirb(current), birb.x.toFloat(), birb.y.toFloat(), null)
        current++
        if(current > birb.maxFrame)
            current = 0
        birb.currentFrame = current
    }

    //Here we are rendering the background
    private fun render(canvas: Canvas?){
        Log.d(TAG, "Render Canvas")
        var bitmapImage : Bitmap = BitmapFactory.decodeResource(resources, R.drawable.run_background)
        bitmapImage = ScaleResize(bitmapImage)

        backgroundImage.x = backgroundImage.x - velocity
        if(backgroundImage.x < -bitmapImage.width) {
            backgroundImage.x = 0
        }
        canvas?.drawBitmap(bitmapImage, (backgroundImage.x).toFloat(),(backgroundImage.y).toFloat(), null)

        //Looping that image so it no longer cuts at a wonky state.
        if(backgroundImage.x < -bitmapImage.width - ScreenSize.SCREEN_WIDTH){
            canvas!!.drawBitmap(bitmapImage, (backgroundImage.x + bitmapImage.width).toFloat(), (backgroundImage.y).toFloat(), null)
        }

    }

    //Let's make the screen fit into the application's full screen
    private fun ScaleResize(bitmap: Bitmap): Bitmap {
        var ratio : Float = (bitmap.width / bitmap.height).toFloat()
        val scaleWidth : Int = (ratio * ScreenSize.SCREEN_HEIGHT).toInt()
        return Bitmap.createScaledBitmap(bitmap, scaleWidth, ScreenSize.SCREEN_HEIGHT, false)

    }

}