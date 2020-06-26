/*
 * Copyright (C) 2020 Hamidreza Etebarian .
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package meow.bottomnavigation

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View

/**
 * Bezier View class.
 *
 * @author  Hamidreza Etebarian
 * @version 1.0.0
 * @since   2019-02-25
 */

class BezierView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0
) : View(context, attrs, defStyleAttrs) {

    private var mainPaint: Paint? = null
    private var shadowPaint: Paint? = null
    private var mainPath: Path? = null
    private var shadowPath: Path? = null
    private var outerArray: Array<PointF>
    private var innerArray: Array<PointF>
    private var progressArray: Array<PointF>

    private var width = 0f
    private var height = 0f
    private var bezierOuterWidth = 0f
    private var bezierOuterHeight = 0f
    private var bezierInnerWidth = 0f
    private var bezierInnerHeight = 0f
    private val shadowHeight = 8f.dp(context)

    var color = 0
        set(value) {
            field = value
            mainPaint?.color = field
            invalidate()
        }
    var shadowColor = 0
        set(value) {
            field = value
            shadowPaint?.setShadowLayer(4f.dp(context), 0f, 0f, shadowColor)
            invalidate()
        }

    var bezierX = 0f
        set(value) {
            if (value == field)
                return
            field = value
            invalidate()
        }

    var progress = 0f
        set(value) {
            if (value == field)
                return
            field = value

            progressArray[1].x = bezierX - bezierInnerWidth / 2
            progressArray[2].x = bezierX - bezierInnerWidth / 4
            progressArray[3].x = bezierX - bezierInnerWidth / 4
            progressArray[4].x = bezierX
            progressArray[5].x = bezierX + bezierInnerWidth / 4
            progressArray[6].x = bezierX + bezierInnerWidth / 4
            progressArray[7].x = bezierX + bezierInnerWidth / 2
            for (i in 2..6) {
                if (progress <= 1f) {//convert to outer
                    progressArray[i].y = calculate(innerArray[i].y, outerArray[i].y)
                } else {
                    progressArray[i].y = calculate(outerArray[i].y, innerArray[i].y)
                }
            }
            if (field == 2f)
                field = 0f

            invalidate()
        }


    init {
        setWillNotDraw(false)

        mainPath = Path()
        shadowPath = Path()
        outerArray = Array(11) { PointF() }
        innerArray = Array(11) { PointF() }
        progressArray = Array(11) { PointF() }

        mainPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mainPaint?.apply {
            strokeWidth = 0f
            isAntiAlias = true
            style = Paint.Style.FILL
            color = this@BezierView.color
        }

        shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        shadowPaint?.apply {
            isAntiAlias = true
            setShadowLayer(4f.dp(context), 0f, 0f, shadowColor)
        }

        color = color
        shadowColor = shadowColor

        setLayerType(LAYER_TYPE_SOFTWARE, shadowPaint)
    }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        width = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        height = MeasureSpec.getSize(heightMeasureSpec).toFloat()
        bezierOuterWidth = 72f.dp(context)
        bezierOuterHeight = 8f.dp(context)
        bezierInnerWidth = 124f.dp(context)
        bezierInnerHeight = 16f.dp(context)

        val extra = shadowHeight
        outerArray[0] = PointF(0f, bezierOuterHeight + extra)
        outerArray[1] = PointF((bezierX - bezierOuterWidth / 2), bezierOuterHeight + extra)
        outerArray[2] = PointF(bezierX - bezierOuterWidth / 4, bezierOuterHeight + extra)
        outerArray[3] = PointF(bezierX - bezierOuterWidth / 4, extra)
        outerArray[4] = PointF(bezierX, extra)
        outerArray[5] = PointF(bezierX + bezierOuterWidth / 4, extra)
        outerArray[6] = PointF(bezierX + bezierOuterWidth / 4, bezierOuterHeight + extra)
        outerArray[7] = PointF(bezierX + bezierOuterWidth / 2, bezierOuterHeight + extra)
        outerArray[8] = PointF(width, bezierOuterHeight + extra)
        outerArray[9] = PointF(width, height)
        outerArray[10] = PointF(0f, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mainPath!!.reset()
        shadowPath!!.reset()

        if (progress == 0f) {
            drawInner(canvas, true)
            drawInner(canvas, false)
        } else {
            drawProgress(canvas, true)
            drawProgress(canvas, false)
        }
    }

    private fun drawInner(canvas: Canvas, isShadow: Boolean) {
        val paint = if (isShadow) shadowPaint else mainPaint
        val path = if (isShadow) shadowPath else mainPath

        calculateInner()

        path!!.lineTo(innerArray[0].x, innerArray[0].y)
        path.lineTo(innerArray[1].x, innerArray[1].y)
        path.cubicTo(
            innerArray[2].x,
            innerArray[2].y,
            innerArray[3].x,
            innerArray[3].y,
            innerArray[4].x,
            innerArray[4].y
        )
        path.cubicTo(
            innerArray[5].x,
            innerArray[5].y,
            innerArray[6].x,
            innerArray[6].y,
            innerArray[7].x,
            innerArray[7].y
        )
        path.lineTo(innerArray[8].x, innerArray[8].y)
        path.lineTo(innerArray[9].x, innerArray[9].y)
        path.lineTo(innerArray[10].x, innerArray[10].y)

        progressArray = innerArray.clone()

        canvas.drawPath(path, paint!!)
    }

    private fun calculateInner() {
        val extra = shadowHeight
        innerArray[0] = PointF(0f, bezierInnerHeight + extra)
        innerArray[1] = PointF((bezierX - bezierInnerWidth / 2), bezierInnerHeight + extra)
        innerArray[2] = PointF(bezierX - bezierInnerWidth / 4, bezierInnerHeight + extra)
        innerArray[3] = PointF(bezierX - bezierInnerWidth / 4, height - extra)
        innerArray[4] = PointF(bezierX, height - extra)
        innerArray[5] = PointF(bezierX + bezierInnerWidth / 4, height - extra)
        innerArray[6] = PointF(bezierX + bezierInnerWidth / 4, bezierInnerHeight + extra)
        innerArray[7] = PointF(bezierX + bezierInnerWidth / 2, bezierInnerHeight + extra)
        innerArray[8] = PointF(width, bezierInnerHeight + extra)
        innerArray[9] = PointF(width, height)
        innerArray[10] = PointF(0f, height)
    }

    private fun drawProgress(canvas: Canvas, isShadow: Boolean) {
        val paint = if (isShadow) shadowPaint else mainPaint
        val path = if (isShadow) shadowPath else mainPath

        path!!.lineTo(progressArray[0].x, progressArray[0].y)
        path.lineTo(progressArray[1].x, progressArray[1].y)
        path.cubicTo(
            progressArray[2].x,
            progressArray[2].y,
            progressArray[3].x,
            progressArray[3].y,
            progressArray[4].x,
            progressArray[4].y
        )
        path.cubicTo(
            progressArray[5].x,
            progressArray[5].y,
            progressArray[6].x,
            progressArray[6].y,
            progressArray[7].x,
            progressArray[7].y
        )
        path.lineTo(progressArray[8].x, progressArray[8].y)
        path.lineTo(progressArray[9].x, progressArray[9].y)
        path.lineTo(progressArray[10].x, progressArray[10].y)

        canvas.drawPath(path, paint!!)
    }

    private fun calculate(start: Float, end: Float): Float {
        var p = progress
        if (p > 1f)
            p = progress - 1f
        if (p in 0.9f..1f)
            calculateInner()
        return (p * (end - start)) + start
    }
}
