package com.example.flappybird.Thread

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.Log
import android.view.SurfaceHolder
import com.example.flappybird.Model.BackgroundImage
import com.example.flappybird.Model.Birb
import com.example.flappybird.Model.Cot
import com.example.flappybird.Model.ScreenSize
import com.example.flappybird.R
import kotlin.collections.ArrayList
import kotlin.random.Random

class PlayThread : Thread{

    private val TAG : String = "PlayThread"
    private var holder : SurfaceHolder
    private var resources : Resources

    private val FPS : Int = (1000.0/60.0).toInt() //Time passed per frame. 60 FPS
    private val backgroundImage = BackgroundImage() //Hello, new object
    private var bitmapImage : Bitmap? = null
    private var startTime : Long = 0
    private var frameTime : Long = 0
    private val velocity = 3
    private val birb : Birb //birb model that will fly

    //Game state: 0 = Stop; 1 = Running; 2 = Game Over
    private var state : Int = 0
    private var velocityBirb : Int = 0

    var cot : Cot? = null
    val numCot = 2
    val velocityCot = 10
    val minY = 250
    val maxY = ScreenSize.SCREEN_HEIGHT - minY - 500
    val kc = ScreenSize.SCREEN_WIDTH * 3/4
    var cotArray : ArrayList<Cot> = arrayListOf()
    var ran : Random = Random

    var iCot = 0
    var isDead = false


    var isRunning : Boolean = false //Flag to Run or Stop
        get() = field
        set(value) {
            field = value
        }


    constructor(holder: SurfaceHolder, resources: Resources) {
        this.holder = holder
        this.resources = resources
        isRunning = true
        //birb
        birb = Birb(resources)

        //background of the game
        bitmapImage = BitmapFactory.decodeResource(resources, R.drawable.run_background)
        bitmapImage = this.bitmapImage?.let { ScaleResize(it) }

        //Cots or otherwise known as insidious Mario Pipes
        cot = Cot(resources)
        createCot(resources)
    }

    private fun createCot(resources: Resources) {
        for (i in 0 until numCot){
            val cot = Cot(resources)
            cot.x = ScreenSize.SCREEN_WIDTH + kc*i
            cot.ccY = ran.nextInt(maxY - minY) + minY
            cotArray.add(cot)
        }
    }

    override fun run() {
        Log.d(TAG, "Thread Started")

        while (isRunning){
            if (holder == null) return
            startTime = System.nanoTime()
            val canvas = holder.lockCanvas()
            if(canvas != null){
                try{
                    synchronized(holder){
                        //rendering the background
                        render(canvas)

                        //rendering the birb on canvas
                        renderBirb(canvas)

                        //rendering the pipes. Damn pipes.
                        renderCot(canvas)
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

    private fun birbDeath() {
        if (isDead){
            isRunning = false
        }
    }

    private fun renderCot(canvas: Canvas?){
        if(state == 1) { //if the game is running
            if (cotArray.get(iCot).x < birb.x - cot!!.w) {
                iCot++
                if (iCot > numCot - 1) {
                    iCot = 0
                }
            } else if (((cotArray.get(iCot).x) < birb.getBirb(0).width) &&
                (cotArray.get(iCot).ccY > birb.y || cotArray.get(iCot).getBottomY() < birb.y + birb.getBirb(0).height)
            )
                isDead = true


            for (i in 0 until numCot) {// 0, 1, 2
                if (cotArray.get(i).x < - cot!!.w){
                    //creating a new cot with x = kc + old_cot
                    cotArray.get(i).x = cotArray.get(i).x + numCot * kc
                    //cot.y is going to be random
                    cotArray.get(i).ccY = ran.nextInt(maxY - minY) + minY

                }
                //moving cot right to left
                if(!isDead) {
                    cotArray.get(i).x = cotArray.get(i).x - velocityCot
                }

                //rendering top pipes
                    canvas!!.drawBitmap(
                        cot!!.cotTop,
                        cotArray.get(i).x.toFloat(),
                        cotArray.get(i).getTopY().toFloat(),
                        null
                    )

                    //rendering bottom pipe (cotTop.x = cotBottom.x)
                    canvas!!.drawBitmap(
                        cot!!.cotBottom,
                        cotArray.get(i).x.toFloat(),
                        cotArray.get(i).getBottomY().toFloat(),
                        null
                    )

            }
        }
    }

    private fun renderBirb(canvas: Canvas?) {
        if(state == 1){
            if(!isDead) {
                if (birb.y < (ScreenSize.SCREEN_HEIGHT - birb.getBirb(0).height)) {
                    velocityBirb = velocityBirb + 2 // fall down
                    birb.y = birb.y + velocityBirb // fly up
                }
            }
        }
        if(!isDead){
        var current : Int = birb.currentFrame
        canvas!!.drawBitmap(birb.getBirb(current), birb.x.toFloat(), birb.y.toFloat(), null)
        current++
        if(current > birb.maxFrame)
            current = 0
        birb.currentFrame = current
        }
    }

    //Here we are rendering the background
    private fun render(canvas: Canvas?){
        Log.d(TAG, "Render Canvas")
        if(!isDead) {
            backgroundImage.x = backgroundImage.x - velocity
        }
        if(backgroundImage.x < -bitmapImage!!.width) {
            backgroundImage.x = 0
        }
        bitmapImage?.let { canvas!!.drawBitmap(it, (backgroundImage.x).toFloat(),(backgroundImage.y).toFloat(), null)}

        //Looping that image so it no longer cuts at a wonky state.
        if(backgroundImage.x < -(bitmapImage!!.width - ScreenSize.SCREEN_WIDTH)){
            bitmapImage?.let {canvas!!.drawBitmap(it,(backgroundImage.x + bitmapImage!!.width).toFloat(), (backgroundImage.y).toFloat(), null)}
        }

    }

    //Let's make the screen fit into the application's full screen
    private fun ScaleResize(bitmap: Bitmap): Bitmap {
        var ratio : Float = (bitmap.width / bitmap.height).toFloat()
        val scaleWidth : Int = (ratio * ScreenSize.SCREEN_HEIGHT).toInt()
        return Bitmap.createScaledBitmap(bitmap, scaleWidth, ScreenSize.SCREEN_HEIGHT, false)

    }

    fun Jump() {
        state = 1

        //Top Screen Fixed. No more flying through it.
        if(birb.y > 0) {
            velocityBirb = -30
        }
    }


}