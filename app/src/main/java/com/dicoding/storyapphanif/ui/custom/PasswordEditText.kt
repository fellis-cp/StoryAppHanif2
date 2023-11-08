package com.dicoding.storyapphanif.ui.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.dicoding.storyapphanif.R

class PasswordEditText : AppCompatEditText {
    private lateinit var passwordIc: Drawable
    private var isPasswordVis = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context , attrs: AttributeSet ,  defStyleAttr: Int) : super (context , attrs , defStyleAttr) {
        init ()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init () {
        passwordIc = ContextCompat.getDrawable(context , R.drawable.ic_baseline_lock_24) as Drawable
        compoundDrawablePadding = 12
        setDrawIcon (passwordIc)
        setHint(R.string.password_hint)

        setOnTouchListener { _, event ->
            val drawableRight = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (right - compoundDrawables[drawableRight].bounds.width())) {
                    togglePwVisibility()
                    return@setOnTouchListener true
                }
            }
            false
        }


        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty() && s.length < 8) {
                    setError(context.getString(R.string.invalid_password), null)
                } else {
                    error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })


    }

    private fun togglePwVisibility() {
        isPasswordVis = !isPasswordVis
        inputType = if (isPasswordVis) {
            InputType.TYPE_CLASS_TEXT
        } else {
            InputType.TYPE_NUMBER_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
        }

        setSelection(text!!.length)
    }


    private fun setDrawIcon(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

}
