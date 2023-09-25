package com.ashraf.nimopaint

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import androidx.compose.ui.graphics.Color

class DrawingView(context: Context, attrs: AttributeSet): View(context, attrs) {
    private var mDrawingPath : CustomPath? = null
    private var mCanvasBitmap: Bitmap? = null
    private var mDrawingPaint: Paint? = null
    private var mCanvasPaint: Paint? = null
    private var mBrushSize: Float = 0.toFloat()
    private var color = android.graphics.Color.BLACK
    private var canvas: Canvas? = null
    private val mPaths = ArrayList<CustomPath>();

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(mCanvasBitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mCanvasBitmap!!, 0f, 0f, mCanvasPaint)

        for(path in mPaths){
            mDrawingPaint!!.strokeWidth = path.brushThickness
            mDrawingPaint!!.color = mDrawingPath!!.color
            canvas.drawPath(path, mDrawingPaint!!)
        }

        if(!mDrawingPath!!.isEmpty){
            mDrawingPaint!!.strokeWidth = mDrawingPath!!.brushThickness
            mDrawingPaint!!.color = mDrawingPath!!.color
            canvas.drawPath(mDrawingPath!!, mDrawingPaint!!)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y

        when(event?.action){
            MotionEvent.ACTION_DOWN ->{
                mDrawingPath!!.color = color
                mDrawingPath!!.brushThickness = mBrushSize

                mDrawingPath!!.reset()
                if (touchX != null) {
                    if (touchY != null) {
                        mDrawingPath!!.moveTo(touchX, touchY)
                    }
                }
            }
            MotionEvent.ACTION_MOVE ->{
                if(touchX != null){
                    if(touchY != null){
                        mDrawingPath!!.lineTo(touchX, touchY)
                    }
                }
            }
            MotionEvent.ACTION_UP->{
                mPaths.add(mDrawingPath!!)
                mDrawingPath = CustomPath(color, mBrushSize)
            }
            else -> return false
        }

        invalidate()

        return true
    }

    fun setSizeForBrush(newSize: Float){
        mBrushSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, newSize, resources.displayMetrics)
        mDrawingPaint!!.strokeWidth = mBrushSize
    }
    init{
        setUpDrawing()
    }

    private fun setUpDrawing(){
        mDrawingPaint = Paint()
        mDrawingPath = CustomPath(color, mBrushSize)
        mDrawingPaint!!.color = color
        mDrawingPaint!!.style = Paint.Style.STROKE
        mDrawingPaint!!.strokeJoin = Paint.Join.ROUND
        mDrawingPaint!!.strokeCap = Paint.Cap.ROUND
        mCanvasPaint = Paint(Paint.DITHER_FLAG)
        //mBrushSize = 20.toFloat()
    }

    internal inner class CustomPath(var color: Int, var brushThickness: Float): Path() {

    }
}