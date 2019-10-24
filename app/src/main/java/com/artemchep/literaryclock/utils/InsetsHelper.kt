package com.artemchep.literaryclock.utils

import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.artemchep.literaryclock.R


fun Fragment.wrapInStatusBarView(child: View): View {
    return LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )

        val statusBarView = View(context).apply {
            layoutParams = LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 0)
            id = R.id.statusBarBg

            val typedValue = TypedValue()
            context.theme.resolveAttribute(R.attr.colorPrimaryDark, typedValue, true)
            setBackgroundResource(typedValue.resourceId)
        }

        addView(statusBarView)
        addView(
            child, ViewGroup.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
    }
}
