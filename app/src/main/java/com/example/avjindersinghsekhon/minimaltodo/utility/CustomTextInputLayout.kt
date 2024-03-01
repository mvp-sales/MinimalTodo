package com.example.avjindersinghsekhon.minimaltodo.utility

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.view.ViewCompat
import com.google.android.material.textfield.TextInputLayout

class CustomTextInputLayout : TextInputLayout {
    private var isHintSet = false
    private var hint: CharSequence? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        if (child is EditText) {
            // Since hint will be nullify on EditText once on parent addView, store hint value locally
            hint = child.hint
        }
        super.addView(child, index, params)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!isHintSet && this.isLaidOut) {
            // We have to reset the previous hint so that equals check pass
            hint = null

            // In case that hint is changed programatically
            editText?.hint?.let { currentEditTextHint ->
                if (currentEditTextHint.isNotEmpty()) {
                    hint = currentEditTextHint
                }
            }
            setHint(hint)
            isHintSet = true
        }
    }
}
