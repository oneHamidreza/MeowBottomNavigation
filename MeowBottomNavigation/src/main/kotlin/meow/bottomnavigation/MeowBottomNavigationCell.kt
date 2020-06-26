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

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.core.view.ViewCompat
import androidx.core.widget.ImageViewCompat
import androidx.databinding.DataBindingUtil
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.etebarian.meowbottomnavigation.R
import com.etebarian.meowbottomnavigation.databinding.MeowNavigationCellBinding


/**
 * Meow Bottom Navigation Cell class.
 *
 * @author  Hamidreza Etebarian
 * @version 1.0.0
 * @since   2019-02-23
 */

@Suppress("unused")
class MeowBottomNavigationCell @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0
) : RelativeLayout(context, attrs, defStyleAttrs) {

    companion object {
        const val EMPTY_VALUE = "empty"
    }

    private val binding = DataBindingUtil.inflate<MeowNavigationCellBinding>(
        LayoutInflater.from(context),
        R.layout.meow_navigation_cell,
        this,
        true
    )

    var defaultIconColor = 0
        set(value) {
            field = value
            if (allowDraw)
                ImageViewCompat.setImageTintList(
                    binding.iv,
                    ofColorStateList(if (!isEnabledCell) defaultIconColor else selectedIconColor)
                )
        }
    var selectedIconColor = 0
        set(value) {
            field = value
            if (allowDraw)
                ImageViewCompat.setImageTintList(
                    binding.iv,
                    ofColorStateList(if (!isEnabledCell) defaultIconColor else selectedIconColor)
                )
        }
    var circleColor = 0
        set(value) {
            field = value
            if (allowDraw)
                isEnabledCell = isEnabledCell
        }

    var icon = 0
        set(value) {
            field = value
            if (allowDraw)
                binding.iv.setImageResource(value)
        }

    var count: String? =
        EMPTY_VALUE
        set(value) {
            field = value
            if (allowDraw) {
                if (count != null && count == EMPTY_VALUE) {
                    binding.tvCount.text = ""
                    binding.tvCount.visibility = View.INVISIBLE
                } else {
                    if (count != null && count?.length ?: 0 >= 3) {
                        field = count?.substring(0, 1) + ".."
                    }
                    binding.tvCount.text = count
                    binding.tvCount.visibility = View.VISIBLE
                    val scale = if (count?.isEmpty() == true) 0.5f else 1f
                    binding.tvCount.scaleX = scale
                    binding.tvCount.scaleY = scale
                }
            }
        }

    private var iconSize = 48f.dp(context)
        set(value) {
            field = value
            if (allowDraw) {
                binding.iv.updateLayoutParams<FrameLayout.LayoutParams> {
                    it.width = value.toInt()
                    it.height = value.toInt()
                }
                binding.iv.pivotX = iconSize / 2f
                binding.iv.pivotY = iconSize / 2f
            }
        }

    var countTextColor = 0
        set(value) {
            field = value
            if (allowDraw)
                binding.tvCount.setTextColor(field)
        }

    var countBackgroundColor = 0
        set(value) {
            field = value
            if (allowDraw) {
                val d = GradientDrawable()
                d.setColor(field)
                d.shape = GradientDrawable.OVAL
                ViewCompat.setBackground(binding.tvCount, d)
            }
        }

    var countTypeface: Typeface? = null
        set(value) {
            field = value
            if (allowDraw && field != null)
                binding.tvCount.typeface = field
        }

    var rippleColor = 0
        set(value) {
            field = value
            if (allowDraw) {
                isEnabledCell = isEnabledCell
            }
        }

    var isFromLeft = false
    var duration = 0L
    private var progress = 0f
        set(value) {
            field = value
            binding.fl.y = (1f - progress) * 18f.dp(context) - 3f.dp(context)

            ImageViewCompat.setImageTintList(
                binding.iv,
                ofColorStateList(if (progress == 1f) selectedIconColor else defaultIconColor)
            )
            val scale = (1f - progress) * (-0.2f) + 1f
            binding.iv.scaleX = scale
            binding.iv.scaleY = scale

            val d = GradientDrawable()
            d.setColor(circleColor)
            d.shape = GradientDrawable.OVAL

            ViewCompat.setBackground(binding.vCircle, d)

            ViewCompat.setElevation(
                binding.vCircle, if (progress > 0.7f) (progress * 4f).dp(context) else 0f
            )

            val m = 24.dp(context)
            binding.vCircle.x =
                (1f - progress) * (if (isFromLeft) -m else m) + ((measuredWidth - 48f.dp(context)) / 2f)
            binding.vCircle.y = (1f - progress) * measuredHeight + 6.dp(context)
        }

    var isEnabledCell = false
        set(value) {
            field = value
            val d = GradientDrawable()
            d.setColor(circleColor)
            d.shape = GradientDrawable.OVAL
            if (Build.VERSION.SDK_INT >= 21 && !isEnabledCell) {
                binding.fl.background = RippleDrawable(ColorStateList.valueOf(rippleColor), null, d)
            } else {
                binding.fl.runAfterDelay(200) {
                    binding.fl.setBackgroundColor(Color.TRANSPARENT)
                }
            }
        }

    var onClickListener: () -> Unit = {}
        set(value) {
            field = value
            binding.iv.setOnClickListener {
                onClickListener()
            }
        }

    private var allowDraw = false

    init {
        allowDraw = true//todo need Delete
        draw()
    }

    private fun draw() {
        if (!allowDraw)
            return

        icon = icon
        count = count
        iconSize = iconSize
        countTextColor = countTextColor
        countBackgroundColor = countBackgroundColor
        countTypeface = countTypeface
        rippleColor = rippleColor
        onClickListener = onClickListener
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        progress = progress
    }

    fun disableCell(isAnimate: Boolean = true) {
        if (isEnabledCell)
            animateProgress(false, isAnimate)
        isEnabledCell = false
    }

    fun enableCell(isAnimate: Boolean = true) {
        if (!isEnabledCell)
            animateProgress(true, isAnimate)
        isEnabledCell = true
    }

    private fun animateProgress(enableCell: Boolean, isAnimate: Boolean = true) {
        val d = if (enableCell) duration else 250
        val anim = ValueAnimator.ofFloat(0f, 1f)
        anim.apply {
            startDelay = if (enableCell) d / 4 else 0L
            duration = if (isAnimate) d else 1L
            interpolator = FastOutSlowInInterpolator()
            addUpdateListener {
                val f = it.animatedFraction
                progress = if (enableCell)
                    f
                else
                    1f - f
            }
            start()
        }
    }
}