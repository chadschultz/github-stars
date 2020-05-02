package com.example.githubstars.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

/**
 * Quick and dirty rip-off of [DividerItemDecoration]. Not all functionality is re-implemented
 * here, but there is one notable benefit: this skips the last divider, so it only draws
 * dividers between items, instead of also drawing one after the last element.
 * Possible further improvements would be to reimplement code from [DividerItemDecoration]
 * to allow for horizontal dividers. Also, a feature could be added to dynamically select whether
 * to draw a divider after the last item or not.
 */
class BetweenItemDecoration(context: Context) : RecyclerView.ItemDecoration() {

    companion object {
        const val TAG = "BetweenItemDecoration"
        val ATTRS = intArrayOf(android.R.attr.listDivider)
    }

    var drawable: Drawable? = null

    private val bounds = Rect()

    init {
        val a = context.obtainStyledAttributes(ATTRS)
        drawable = a.getDrawable(0)
        if (drawable == null) {
            Log.w(
                TAG, "@android:attr/listDivider was not set in the theme used for this "
                        + "DividerItemDecoration. Please set that attribute or call setDrawable()"
            )
        }
        a.recycle()
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        drawable?.let { divider ->
            draw(c, parent, divider)
        }
    }

    private fun draw(canvas: Canvas, parent: RecyclerView, divider: Drawable) {
        canvas.save()
        val left: Int
        val right: Int
        //noinspection AndroidLintNewApi - NewApi lint fails to handle overrides.
        if (parent.clipToPadding) {
            left = parent.paddingLeft
            right = parent.width - parent.paddingRight
            canvas.clipRect(
                left, parent.paddingTop, right,
                parent.height - parent.paddingBottom
            )
        } else {
            left = 0
            right = parent.width
        }

        val childCount = parent.childCount
        for (i in 0 until childCount - 1) {
            val child = parent.getChildAt(i)
            parent.getDecoratedBoundsWithMargins(child, bounds)
            val bottom: Int = bounds.bottom + child.translationY.roundToInt()
            val top: Int = bottom - divider.intrinsicHeight
            divider.setBounds(left, top, right, bottom)
            divider.draw(canvas)
        }
        canvas.restore()
    }
}
