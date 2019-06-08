package com.etebarian.meowbottomnavigation

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View

/**
 * Created by 1HE on 2/25/2019.
 */

class BezierView : View {

    private var mainPaint: Paint? = null
    private var shadowPaint: Paint? = null
    private var mainPath: Path? = null
    private var shadowPath: Path? = null
    private lateinit var outerArray: Array<PointF>
    private lateinit var innerArray: Array<PointF>
    private lateinit var progressArray: Array<PointF>

    private var width = 0f
    private var height = 0f
    private var bezierOuterWidth = 0f
    private var bezierOuterHeight = 0f
    private var bezierInnerWidth = 0f
    private var bezierInnerHeight = 0f
    private val shadowHeight = dipf(context, 8)

    var color = 0
        set(value) {
            field = value
            mainPaint?.color = field
        }
    var shadowColor = 0
        set(value) {
            field = value
            shadowPaint?.setShadowLayer(dipf(context, 4), 0f, 0f, shadowColor)
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

    @SuppressLint("NewApi")
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initializeViews()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initializeViews()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initializeViews()
    }

    constructor(context: Context) : super(context) {
        initializeViews()
    }

    private fun initializeViews() {
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
            setShadowLayer(dipf(context, 4), 0f, 0f, shadowColor)
        }

        color = color
        shadowColor = shadowColor

        setLayerType(View.LAYER_TYPE_SOFTWARE, shadowPaint)
    }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        width = View.MeasureSpec.getSize(widthMeasureSpec).toFloat()
        height = View.MeasureSpec.getSize(heightMeasureSpec).toFloat()
        bezierOuterWidth = dipf(context, 72)
        bezierOuterHeight = dipf(context, 8)
        bezierInnerWidth = dipf(context, 124)
        bezierInnerHeight = dipf(context, 16)

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
        path.cubicTo(innerArray[2].x, innerArray[2].y, innerArray[3].x, innerArray[3].y, innerArray[4].x, innerArray[4].y)
        path.cubicTo(innerArray[5].x, innerArray[5].y, innerArray[6].x, innerArray[6].y, innerArray[7].x, innerArray[7].y)
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
        path.cubicTo(progressArray[2].x, progressArray[2].y, progressArray[3].x, progressArray[3].y, progressArray[4].x, progressArray[4].y)
        path.cubicTo(progressArray[5].x, progressArray[5].y, progressArray[6].x, progressArray[6].y, progressArray[7].x, progressArray[7].y)
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
