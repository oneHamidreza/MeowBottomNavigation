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

@file:Suppress("unused")

package meow.bottomnavigation

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.util.LayoutDirection
import android.util.Log
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.etebarian.meowbottomnavigation.R
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Meow Bottom Navigation Cell class.
 *
 * @author  Hamidreza Etebarian
 * @version 1.0.0
 * @since   2019-02-10
 */

internal typealias IBottomNavigationListener = (model: MeowBottomNavigation.Model) -> Unit

@Suppress("MemberVisibilityCanBePrivate")
class MeowBottomNavigation : FrameLayout {

    var models = ArrayList<Model>()
    var cells = ArrayList<MeowBottomNavigationCell>()
        private set
    private var callListenerWhenIsSelected = false

    private var selectedId = -1

    private var onClickedListener: IBottomNavigationListener = {}
    private var onShowListener: IBottomNavigationListener = {}
    private var onReselectListener: IBottomNavigationListener = {}

    private var heightCell = 0
    private var isAnimating = false

    var defaultIconColor = Color.parseColor("#757575")
        set(value) {
            field = value
            updateAllIfAllowDraw()
        }
    var selectedIconColor = Color.parseColor("#2196f3")
        set(value) {
            field = value
            updateAllIfAllowDraw()
        }
    var backgroundBottomColor = Color.parseColor("#ffffff")
        set(value) {
            field = value
            updateAllIfAllowDraw()
        }

    var circleColor = Color.parseColor("#ffffff")
        set(value) {
            field = value
            updateAllIfAllowDraw()
        }
    private var shadowColor = -0x454546
    var countTextColor = Color.parseColor("#ffffff")
        set(value) {
            field = value
            updateAllIfAllowDraw()
        }
    var titleTextColor = Color.parseColor("#000000")
        set(value) {
            field = value
            updateAllIfAllowDraw()
        }
    var countBackgroundColor = Color.parseColor("#ff0000")
        set(value) {
            field = value
            updateAllIfAllowDraw()
        }
    var countTypeface: Typeface? = null
        set(value) {
            field = value
            updateAllIfAllowDraw()
        }
    var hasAnimation: Boolean = true
        set(value) {
            field = value
            updateAllIfAllowDraw()
        }
    private var rippleColor = Color.parseColor("#757575")

    private var allowDraw = false


    @Suppress("PrivatePropertyName")
    private lateinit var ll_cells: LinearLayout
    private lateinit var bezierView: BezierView

    init {
        heightCell = context.resources.getDimension(R.dimen._30sdp).toInt()
        heightCell=heightCell.dp(context)
    }

    constructor(context: Context) : super(context) {
        initializeViews()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setAttributeFromXml(context, attrs)
        initializeViews()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setAttributeFromXml(context, attrs)
        initializeViews()
    }

    private fun setAttributeFromXml(context: Context, attrs: AttributeSet) {
        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.MeowBottomNavigation, 0, 0
        )
        try {
            a.apply {
                defaultIconColor = getColor(
                    R.styleable.MeowBottomNavigation_mbn_defaultIconColor,
                    defaultIconColor
                )
                selectedIconColor = getColor(
                    R.styleable.MeowBottomNavigation_mbn_selectedIconColor,
                    selectedIconColor
                )
                backgroundBottomColor = getColor(
                    R.styleable.MeowBottomNavigation_mbn_backgroundBottomColor,
                    backgroundBottomColor
                )
                heightCell =
                    getDimension(R.styleable.MeowBottomNavigation_mbn_height,heightCell.toFloat()).roundToInt()

                circleColor =
                    getColor(R.styleable.MeowBottomNavigation_mbn_circleColor, circleColor)
                countTextColor =
                    getColor(R.styleable.MeowBottomNavigation_mbn_countTextColor, countTextColor)
                titleTextColor =
                    getColor(R.styleable.MeowBottomNavigation_mbn_titleTextColor, titleTextColor)
                countBackgroundColor = getColor(
                    R.styleable.MeowBottomNavigation_mbn_countBackgroundColor,
                    countBackgroundColor
                )
                rippleColor =
                    getColor(R.styleable.MeowBottomNavigation_mbn_rippleColor, rippleColor)
                shadowColor =
                    getColor(R.styleable.MeowBottomNavigation_mbn_shadowColor, shadowColor)

                val typeface = getString(R.styleable.MeowBottomNavigation_mbn_countTypeface)
                if (typeface != null && typeface.isNotEmpty())
                    countTypeface = Typeface.createFromAsset(context.assets, typeface)

                hasAnimation =
                    getBoolean(R.styleable.MeowBottomNavigation_mbn_hasAnimation, hasAnimation)
            }
        } finally {
            a.recycle()
        }
    }

    private fun initializeViews() {
        ll_cells = LinearLayout(context)
        ll_cells.apply {
            val params = LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, heightCell)
            params.gravity = Gravity.BOTTOM
            layoutParams = params
            orientation = LinearLayout.HORIZONTAL
            clipChildren = false
            clipToPadding = false
        }

        bezierView = BezierView(context)
        bezierView.apply {
            layoutParams = LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, heightCell)
            color = backgroundBottomColor
            shadowColor = this@MeowBottomNavigation.shadowColor
        }
        Log.e("DIMEN", heightCell.toString())
        addView(bezierView)
        addView(ll_cells)
        allowDraw = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (selectedId == -1) {
            bezierView.bezierX =
                if (Build.VERSION.SDK_INT >= 17 && layoutDirection == LayoutDirection.RTL) measuredWidth + 72f.dp(
                    context
                ) else (-72f).dp(context)
        }
        if (selectedId != -1) {
            show(selectedId, false)
        }
    }

    fun add(model: Model) {
        val cell = MeowBottomNavigationCell(context)
        cell.apply {
            val params = LinearLayout.LayoutParams(0, heightCell, 1f)
            layoutParams = params
            icon = model.icon
            count = model.count
            title=model.title
            defaultIconColor = this@MeowBottomNavigation.defaultIconColor
            selectedIconColor = this@MeowBottomNavigation.selectedIconColor
            circleColor = this@MeowBottomNavigation.circleColor
            countTextColor = this@MeowBottomNavigation.countTextColor
            titleTextColor = this@MeowBottomNavigation.titleTextColor
            countBackgroundColor = this@MeowBottomNavigation.countBackgroundColor
            countTypeface = this@MeowBottomNavigation.countTypeface
            rippleColor = this@MeowBottomNavigation.rippleColor
            onClickListener = {
                if (isShowing(model.id)) // added for https://github.com/shetmobile/MeowBottomNavigation/issues/39
                    onReselectListener(model)

                if (!cell.isEnabledCell && !isAnimating) {
                    show(model.id, hasAnimation)
                    onClickedListener(model)
                } else {
                    if (callListenerWhenIsSelected)
                        onClickedListener(model)
                }
            }
            disableCell(hasAnimation)
            ll_cells.addView(this)
        }

        cells.add(cell)
        models.add(model)
    }

    private fun updateAllIfAllowDraw() {
        if (!allowDraw)
            return

        cells.forEach {
            it.defaultIconColor = defaultIconColor
            it.selectedIconColor = selectedIconColor
            it.circleColor = circleColor
            it.countTextColor = countTextColor
            it.countBackgroundColor = countBackgroundColor
            it.countTypeface = countTypeface
        }

        bezierView.color = backgroundBottomColor
    }

    private fun anim(cell: MeowBottomNavigationCell, id: Int, enableAnimation: Boolean = true) {
        isAnimating = true

        val pos = getModelPosition(id)
        val nowPos = getModelPosition(selectedId)

        val nPos = if (nowPos < 0) 0 else nowPos
        val dif = abs(pos - nPos)
        val d = (dif) * 100L + 150L

        val animDuration = if (enableAnimation && hasAnimation) d else 1L
        val animInterpolator = FastOutSlowInInterpolator()

        val anim = ValueAnimator.ofFloat(0f, 1f)
        anim.apply {
            duration = animDuration
            interpolator = animInterpolator
            val beforeX = bezierView.bezierX
            addUpdateListener {
                val f = it.animatedFraction
                val newX = cell.x + (cell.measuredWidth / 2)
                if (newX > beforeX)
                    bezierView.bezierX = f * (newX - beforeX) + beforeX
                else
                    bezierView.bezierX = beforeX - f * (beforeX - newX)
                if (f == 1f)
                    isAnimating = false
            }
            start()
        }

        if (abs(pos - nowPos) > 1) {
            val progressAnim = ValueAnimator.ofFloat(0f, 1f)
            progressAnim.apply {
                duration = animDuration
                interpolator = animInterpolator
                addUpdateListener {
                    val f = it.animatedFraction
                    bezierView.progress = f * 2f

                }
                start()
            }
        }

        cell.isFromLeft = pos > nowPos
        cells.forEach {
            it.duration = d
        }
    }

    fun show(id: Int, enableAnimation: Boolean = true) {
        for (i in models.indices) {
            val model = models[i]
            val cell = cells[i]
            if (model.id == id) {
                anim(cell, id, enableAnimation)
                cell.enableCell(enableAnimation)
                onShowListener(model)
            } else {
                cell.disableCell(hasAnimation)
            }
        }
        selectedId = id
    }

    fun isShowing(id: Int): Boolean {
        return selectedId == id
    }

    fun getModelById(id: Int): Model? {
        models.forEach {
            if (it.id == id)
                return it
        }
        return null
    }

    fun getCellById(id: Int): MeowBottomNavigationCell? {
        return cells[getModelPosition(id)]
    }

    fun getModelPosition(id: Int): Int {
        for (i in models.indices) {
            val item = models[i]
            if (item.id == id)
                return i
        }
        return -1
    }

    fun setCount(id: Int, count: String) {
        val model = getModelById(id) ?: return
        val pos = getModelPosition(id)
        model.count = count
        cells[pos].count = count
    }

    fun clearCount(id: Int) {
        val model = getModelById(id) ?: return
        val pos = getModelPosition(id)
        model.count = MeowBottomNavigationCell.EMPTY_VALUE
        cells[pos].count = MeowBottomNavigationCell.EMPTY_VALUE
    }

    fun clearAllCounts() {
        models.forEach {
            clearCount(it.id)
        }
    }

    fun setOnShowListener(listener: IBottomNavigationListener) {
        onShowListener = listener
    }

    fun setOnClickMenuListener(listener: IBottomNavigationListener) {
        onClickedListener = listener
    }

    fun setOnReselectListener(listener: IBottomNavigationListener) {
        onReselectListener = listener
    }

    class Model(var id: Int,var icon: Int,var title: String) {
        var count: String = MeowBottomNavigationCell.EMPTY_VALUE

    }
}