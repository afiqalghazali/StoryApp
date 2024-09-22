package com.scifi.storyapp.view.customView

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import com.google.android.material.textfield.TextInputEditText
import com.scifi.storyapp.R

class HintEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : TextInputEditText(context, attrs) {

    init {
        addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (inputType and InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS) {
                    if (!Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                        setError(context.getString(R.string.email_error), null)
                    } else {
                        error = null
                    }
                } else {
                    if (s.length < 8) {
                        setError(context.getString(R.string.password_error), null)
                    } else {
                        error = null
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }
}
