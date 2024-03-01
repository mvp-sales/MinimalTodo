package com.example.avjindersinghsekhon.minimaltodo.utility

import android.content.Context
import com.example.avjindersinghsekhon.minimaltodo.R

object Utils {
    @JvmStatic
    fun getToolbarHeight(context: Context): Int {
        val styledAttributes = context.theme.obtainStyledAttributes(
                intArrayOf(R.attr.actionBarSize)
        )
        val toolbarHeight = styledAttributes.getDimension(0, 0f).toInt()
        styledAttributes.recycle()
        return toolbarHeight
    }
}
