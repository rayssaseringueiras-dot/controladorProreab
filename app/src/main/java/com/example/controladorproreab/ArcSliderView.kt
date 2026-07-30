package com.example.controladorproreab
// Aqui é o de inclinação

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.pow

class ArcSliderView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val bgPaint = Paint().apply {
        color = Color.LTGRAY
        strokeWidth = 20f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
    }

    private val arcPaint = Paint().apply {
        color = Color.parseColor("#4A4AE0")
        strokeWidth = 20f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
    }


    private val thumbPaint = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val activePaint = Paint().apply {
        color = Color.BLUE
        strokeWidth = 16f
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
    }

    private val textPaint = Paint().apply {
        color = Color.BLACK
        textSize = 35f
        isFakeBoldText = true
        isAntiAlias = true
    }

    private var progress = 0f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val startX = width * 0.25f
        val startY = height * 0.15f

        val endX = width * 0.78f
        val endY = height * 0.82f

        val path = Path()
        path.moveTo(startX, startY)

        path.cubicTo(
            startX + 30f, startY + 180f,
            endX - 120f, endY,
            endX, endY
        ) // Essa parte vai arrumar a curva

        canvas.drawPath(path, bgPaint)
        val measure = PathMeasure(path, false)

        val activePath = Path()

        measure.getSegment(
            measure.length * (1f - progress),
            measure.length,
            activePath,
            true
        )

        canvas.drawPath(activePath, arcPaint)

        val t = 1f - progress // Posição da bolinha na curva
//        val botom = -40f
        val x = cubicBezier(t, startX, startX + 30f, endX - 120f, endX)
        val y = cubicBezier(t, startY, startY + 180f, endY, endY)

//        canvas.drawLine(x, y, x, botom, activePaint)
        canvas.drawCircle(x, y, 20f, thumbPaint)

        // Texto
        canvas.drawText("90°", startX - 20, startY - 15, textPaint)
        canvas.drawText("0", endX - 5, endY + 45, textPaint)

        // Valor atual
        val currentAngle = (progress * 90).toInt()
        canvas.drawText("$currentAngle°", x - 20, y - 30, textPaint)
    }

    private fun cubicBezier(
        t: Float,
        p0: Float,
        p1: Float,
        p2: Float,
        p3: Float
    ): Float {
        return ((1 - t).pow(3) * p0 +
                3 * (1 - t).pow(2) * t * p1 +
                3 * (1 - t) * t * t * p2 +
                t.pow(3) * p3)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        progress = 1f - (event.y / height)
        progress = progress.coerceIn(0f, 1f)

        invalidate()


        val valorAtual = getAngleValue()

        // chama a ControladorActivity
        (context as? ControladorActivity)?.onAngleChanged(valorAtual)

        return true
    }

    fun getAngleValue(): Int = (progress * 90).toInt()
}