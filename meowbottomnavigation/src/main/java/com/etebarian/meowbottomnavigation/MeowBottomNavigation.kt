@file:Suppress("unused")

package com.etebarian.meowbottomnavigation

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Handler
import android.util.AttributeSet
import android.util.LayoutDirection
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import kotlin.math.abs

/**
 * Created by 1HE on 10/23/2018.
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
    private var rippleColor = Color.parseColor("#757575")

    private var allowDraw = false
    private var animDuration = 1L
    private var isCustomAnimDuration = false
    private var customDuration = 1L

    @Suppress("PrivatePropertyName")
    private lateinit var ll_cells: LinearLayout
    private lateinit var bezierView: BezierView

    init {
        heightCell = dip(context, 72)
    }

    constructor(context: Context) : super(context) {
        initializeViews()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setAttributeFromXml(context, attrs)
        initializeViews()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setAttributeFromXml(context, attrs)
        initializeViews()
    }

    private fun setAttributeFromXml(context: Context, attrs: AttributeSet) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.MeowBottomNavigation, 0, 0)
        try {
            a.apply {
                defaultIconColor = getColor(R.styleable.MeowBottomNavigation_mbn_defaultIconColor, defaultIconColor)
                selectedIconColor = getColor(R.styleable.MeowBottomNavigation_mbn_selectedIconColor, selectedIconColor)
                backgroundBottomColor = getColor(R.styleable.MeowBottomNavigation_mbn_backgroundBottomColor, backgroundBottomColor)
                circleColor = getColor(R.styleable.MeowBottomNavigation_mbn_circleColor, circleColor)
                countTextColor = getColor(R.styleable.MeowBottomNavigation_mbn_countTextColor, countTextColor)
                countBackgroundColor = getColor(R.styleable.MeowBottomNavigation_mbn_countBackgroundColor, countBackgroundColor)
                rippleColor = getColor(R.styleable.MeowBottomNavigation_mbn_rippleColor, rippleColor)
                shadowColor = getColor(R.styleable.MeowBottomNavigation_mbn_shadowColor, shadowColor)

                val typeface = getString(R.styleable.MeowBottomNavigation_mbn_countTypeface)
                if (typeface != null && typeface.isNotEmpty())
                    countTypeface = Typeface.createFromAsset(context.assets, typeface)
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

        addView(bezierView)
        addView(ll_cells)
        allowDraw = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (selectedId == -1) {
            bezierView.bezierX = if (Build.VERSION.SDK_INT >= 17 && layoutDirection == LayoutDirection.RTL) measuredWidth + dipf(context, 72) else -dipf(context, 72)
        }
        if (selectedId != -1) {
            show(selectedId, false)
        }
    }

    fun add(model: Model) {
        val cell = MeowBottomNavigationCell(context, model.useOriginColor)
        cell.apply {
            val params = LinearLayout.LayoutParams(0, heightCell, 1f)
            layoutParams = params
            icon = model.icon
            count = model.count
            defaultIconColor = this@MeowBottomNavigation.defaultIconColor
            selectedIconColor = this@MeowBottomNavigation.selectedIconColor
            circleColor = this@MeowBottomNavigation.circleColor
            countTextColor = this@MeowBottomNavigation.countTextColor
            countBackgroundColor = this@MeowBottomNavigation.countBackgroundColor
            countTypeface = this@MeowBottomNavigation.countTypeface
            rippleColor = this@MeowBottomNavigation.rippleColor
            onClickListener = {
                if (isShowing(model.id)) {
                    // added for https://github.com/shetmobile/MeowBottomNavigation/issues/39
                    onReselectListener(model)
                }

                if (!cell.isEnabledCell && !isAnimating) {
                    show(model.id)
                    onClickedListener(model)
                } else {
                    if (callListenerWhenIsSelected) {
                        onClickedListener(model)
                    }
                }
            }
            disableCell()
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

        //animDuration = if (enableAnimation) d else 1L
        animDuration = if (isCustomAnimDuration) customDuration else if (enableAnimation) d else 1L
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
                cell.enableCell()
                onShowListener(model).apply {
                    simpleDelay {
                        model.endAnimation?.invoke()
                    }
                }
            } else {
                cell.disableCell()
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

    fun animDuration(animDuration: Long) {
        isCustomAnimDuration = true
        this.animDuration = animDuration
    }

    private fun simpleDelay(action: () -> Unit) {
        Handler().postDelayed({
            action.invoke()
        }, animDuration + 200L)
    }

    class Model(var id: Int, var icon: Int, var endAnimation: (() -> Unit)? = null, var useOriginColor: Boolean = false) {
        var count: String = MeowBottomNavigationCell.EMPTY_VALUE
    }
}