@file:JvmName("UIUtils")

package com.matisse.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.DisplayMetrics
import android.util.TypedValue
import android.util.TypedValue.applyDimension
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.matisse.R
import com.matisse.entity.IncapableCause
import com.matisse.widget.IncapableDialog
import kotlin.math.min
import kotlin.math.roundToInt

const val MIN_GRID_WIDTH = 200              // min width of media grid
const val MAX_SPAN_COUNT = 6                // max span of media grid

fun handleCause(context: Context, cause: IncapableCause?) {
    if (cause?.noticeEvent != null) {
        cause.noticeEvent?.invoke(
            context, cause.form, cause.title ?: "", cause.message ?: ""
        )
        return
    }

    when (cause?.form) {
        IncapableCause.DIALOG -> {
            val incapableDialog = IncapableDialog.newInstance(cause.title, cause.message)
            incapableDialog.show(
                (context as FragmentActivity).supportFragmentManager,
                IncapableDialog::class.java.name
            )
        }

        IncapableCause.TOAST -> {
            Toast.makeText(context, cause.message, Toast.LENGTH_SHORT).show()
        }
    }
}

fun spanCount(context: Context, gridExpectedSize: Int): Int {
    if (gridExpectedSize < MIN_GRID_WIDTH) {
        return MAX_SPAN_COUNT
    }

    val screenWidth = context.resources.displayMetrics.widthPixels
    val expected = screenWidth / gridExpectedSize
    var spanCount = expected.toFloat().roundToInt()
    spanCount = min(spanCount, MAX_SPAN_COUNT)
    if (spanCount == 0) spanCount = 1

    return spanCount
}

fun setTextDrawable(context: Context, textView: TextView?, attr: Int) {
    if (textView == null) return

    val drawables = textView.compoundDrawables
    val ta = context.theme.obtainStyledAttributes(intArrayOf(attr))
    val color = ta.getColor(0, 0)
    ta.recycle()

    for (i in drawables.indices) {
        val drawable = drawables[i]
        if (drawable != null) {
            val state = drawable.constantState ?: continue

            drawables[i] = state.newDrawable().mutate().apply {
                colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
                bounds = drawable.bounds
            }
        }
    }

    textView.setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3])
}

/**
 * ??????attr????????????????????????
 */
fun obtainAttrString(context: Context, attr: Int, defaultRes: Int = R.string.button_null): Int {
    val ta = context.theme.obtainStyledAttributes(intArrayOf(attr)) ?: return defaultRes
    val stringRes = ta.getResourceId(0, defaultRes)
    ta.recycle()

    return stringRes
}

/**
 * ????????????????????????
 * ???????????????????????????????????????????????????
 *
 * @param isVisible true visible
 * @param view      targetView
 */
fun setViewVisible(isVisible: Boolean, view: View?) {
    if (view == null) return
    val visibleFlag = if (isVisible) View.VISIBLE else View.GONE

    if (view.visibility != visibleFlag) {
        view.visibility = visibleFlag
    }
}

fun dp2px(context: Context, dipValue: Float): Float {
    val mDisplayMetrics = getDisplayMetrics(context)
    return applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, mDisplayMetrics)
}

/**
 * ???????????????????????????.
 * @param context the context
 * @return mDisplayMetrics
 */
private fun getDisplayMetrics(context: Context?): DisplayMetrics {
    val mResources: Resources = if (context == null) {
        Resources.getSystem()
    } else {
        context.resources
    }
    return mResources.displayMetrics
}

/**
 * ?????????????????????px
 *
 * @param context ?????????
 * @return ?????????px
 */
fun getScreenWidth(context: Context): Int {
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val outMetrics = DisplayMetrics()// ?????????????????????
    windowManager.defaultDisplay.getMetrics(outMetrics)// ?????????????????????
    return outMetrics.widthPixels
}

/**
 * ?????????????????????px
 * @param context ?????????
 * @return ?????????px
 */
fun getScreenHeight(context: Context): Int {
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val outMetrics = DisplayMetrics()// ?????????????????????
    windowManager.defaultDisplay.getMetrics(outMetrics)// ?????????????????????
    return outMetrics.heightPixels
}

fun setOnClickListener(clickListener: View.OnClickListener, vararg view: View) {
    view.forEach {
        it.setOnClickListener(clickListener)
    }
}