package com.example.controladorproreab
// Aqui é o de altura

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class VerticalSliderView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val bgPaint = Paint().apply {
        color = Color.LTGRAY
        strokeWidth = 16f
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
    }

    private val activePaint = Paint().apply {
        color = Color.BLUE
        strokeWidth = 16f
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
    }

    private val thumbPaint = Paint().apply {
        color = Color.BLUE
        isAntiAlias = true
    }

    private val textPaint = Paint().apply {
        color = Color.BLACK
        textSize = 30f
        isFakeBoldText = true
        isAntiAlias = true
    }

    private var progress = 0f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val x = width / 2f
        val top = 40f
        val bottom = height - 40f

        canvas.drawLine(x, top, x, bottom, bgPaint)

        val y = bottom - ((bottom - top) * progress)

        canvas.drawLine(x, y, x, bottom, activePaint)
        canvas.drawCircle(x, y, 18f, thumbPaint)

        canvas.drawText("120CM", x + 25, top + 10, textPaint)
        canvas.drawText("40CM", x + 25, bottom + 10, textPaint)

        val currentHeight = (40 + (80 * progress)).toInt()
        canvas.drawText("${currentHeight}CM", x + 25, y + 10, textPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        progress = 1f - (event.y / height)
        progress = progress.coerceIn(0f, 1f)

        invalidate()

        val valorAtual = getHeightValue()  // Obtém o valor atual da altura

        (context as? ControladorActivity)?.onHeightChanged(valorAtual) // Chama a função da ControladorActivity

        return true
    }

    fun getHeightValue(): Int = (40 + (80 * progress)).toInt()

    fun setHeightValue(height: Int) {

        val safeHeight = height.coerceIn(40, 120)

        progress = (safeHeight - 40) / 80f


        invalidate()
    }
}