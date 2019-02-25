package com.etebarian.meowbottomnavigation

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

/**
 * Created by 1HE on 2/23/2019.
 */

@Suppress("unused", "LeakingThis", "MemberVisibilityCanBePrivate")
internal class CellImageView : AppCompatImageView {

    var isBitmap = false
        set(value) {
            field = value
            draw()
        }
    var useColor = true
        set(value) {
            field = value
            draw()
        }
    var resource = 0
        set(value) {
            field = value
            draw()
        }
    var color = 0
        set(value) {
            field = value
            draw()
        }
    var size = dip(context, 24)
        set(value) {
            field = value
            requestLayout()
        }
    private var actionBackgroundAlpha = false
    private var changeSize = true
    private var fitImage = false
    private var colorAnimator: ValueAnimator? = null
    private var allowDraw = false

    constructor(context: Context) : super(context) {
        initializeView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setAttributeFromXml(context, attrs)
        initializeView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setAttributeFromXml(context, attrs)
        initializeView()
    }

    private fun setAttributeFromXml(context: Context, attrs: AttributeSet) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.CellImageView, 0, 0)
        try {
            a?.apply {
                isBitmap = getBoolean(R.styleable.CellImageView_meow_imageview_isBitmap, isBitmap)
                useColor = getBoolean(R.styleable.CellImageView_meow_imageview_useColor, useColor)
                resource = getResourceId(R.styleable.CellImageView_meow_imageview_resource, resource)
                color = getColor(R.styleable.CellImageView_meow_imageview_color, color)
                size = getDimensionPixelSize(R.styleable.CellImageView_meow_imageview_size, size)
                actionBackgroundAlpha = getBoolean(R.styleable.CellImageView_meow_imageview_actionBackgroundAlpha, actionBackgroundAlpha)
                changeSize = getBoolean(R.styleable.CellImageView_meow_imageview_changeSize, changeSize)
                fitImage = getBoolean(R.styleable.CellImageView_meow_imageview_fitImage, fitImage)
            }
        } finally {
            a?.recycle()
        }
    }

    private fun initializeView() {
        allowDraw = true
        draw()
    }

    private fun draw() {
        if (!allowDraw)
            return

        if (resource == 0)
            return

        if (isBitmap) {
            try {
                val drawable = if (color == 0) context.getDrawableCompat(resource) else DrawableHelper.changeColorDrawableRes(context, resource, color)
                setImageDrawable(drawable)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return
        }

        if (useColor && color == 0)
            return

        val c = if (useColor) color else -2
        try {
            setImageDrawable(DrawableHelper.changeColorDrawableVector(context, resource, c))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun changeColorByAnim(newColor: Int, d: Long = 250L) {
        if (color == 0) {
            color = newColor
            return
        }
        val lastColor = color

        colorAnimator?.cancel()

        colorAnimator = ValueAnimator.ofFloat(0f, 1f)
        colorAnimator?.apply {
            duration = d
            interpolator = FastOutSlowInInterpolator()
            addUpdateListener { animation ->
                val f = animation.animatedFraction
                color = ColorHelper.mixTwoColors(newColor, lastColor, f)
            }
            start()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (fitImage) {
            val d = drawable
            if (d != null) {
                val width = MeasureSpec.getSize(widthMeasureSpec)
                val height = Math.ceil((width.toFloat() * d.intrinsicHeight.toFloat() / d.intrinsicWidth).toDouble()).toInt()
                setMeasuredDimension(width, height)
            } else {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            }
            return
        }

        if (isBitmap || !changeSize) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }

        val newSize = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY)
        super.onMeasure(newSize, newSize)
    }

}