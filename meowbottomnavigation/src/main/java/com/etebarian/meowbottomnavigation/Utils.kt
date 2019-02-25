package com.etebarian.meowbottomnavigation

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import java.lang.Exception

/**
 * Created by 1HE on 2/23/2019.
 */

private fun getDP(context: Context) = context.resources.displayMetrics.density

internal fun dipf(context: Context,f: Float) = f * getDP(context)

internal fun dipf(context: Context,i: Int) = i * getDP(context)

internal fun dip(context: Context,i: Int) = (i * getDP(context)).toInt()

internal object DrawableHelper{

    fun changeColorDrawableVector(c: Context?, resDrawable: Int, color: Int): Drawable? {
        if (c == null)
            return null

        val d = VectorDrawableCompat.create(c.resources, resDrawable, null) ?: return null
        d.mutate()
        if (color != -2)
            d.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        return d
    }

    fun changeColorDrawableRes(c: Context?, resDrawable: Int, color: Int): Drawable? {
        if (c == null)
            return null

        val d = ContextCompat.getDrawable(c, resDrawable) ?: return null
        d.mutate()
        if (color != -2)
            d.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        return d
    }
}

internal object ColorHelper{

    fun mixTwoColors(color1: Int, color2: Int, amount: Float): Int {
        val alphaChannel = 24
        val redChannel = 16
        val greenChannel = 8

        val inverseAmount = 1.0f - amount

        val a = ((color1 shr alphaChannel and 0xff).toFloat() * amount + (color2 shr alphaChannel and 0xff).toFloat() * inverseAmount).toInt() and 0xff
        val r = ((color1 shr redChannel and 0xff).toFloat() * amount + (color2 shr redChannel and 0xff).toFloat() * inverseAmount).toInt() and 0xff
        val g = ((color1 shr greenChannel and 0xff).toFloat() * amount + (color2 shr greenChannel and 0xff).toFloat() * inverseAmount).toInt() and 0xff
        val b = ((color1 and 0xff).toFloat() * amount + (color2 and 0xff).toFloat() * inverseAmount).toInt() and 0xff

        return a shl alphaChannel or (r shl redChannel) or (g shl greenChannel) or b
    }
}

internal fun Context.getDrawableCompat(res: Int) = ContextCompat.getDrawable(this, res)

internal inline fun <T : View?> T.runAfterDelay(delay: Long, crossinline f: T.() -> Unit) {
    this?.postDelayed({
        try { f() }catch (e:Exception){}
    }, delay)
}
