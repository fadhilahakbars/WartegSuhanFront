package com.netlab.suhan.utils

import android.content.Context
import android.graphics.Rect
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.widget.EditText
import com.google.android.material.textfield.TextInputEditText
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class CurrencyEditText : TextInputEditText {
    private var currencyTextWatcher = CurrencyTextWatcher(this)

    constructor(ctx: Context) : super(ctx)

    constructor(ctx: Context, attr: AttributeSet) :
            super(ctx, attr)

    constructor(ctx: Context, attr: AttributeSet, defStyleAttr: Int) :
            super(ctx, attr, defStyleAttr)

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if (focused) this.addTextChangedListener(currencyTextWatcher)
        else this.removeTextChangedListener(currencyTextWatcher)
    }

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            this.clearFocus()

        return super.onKeyPreIme(keyCode, event)
    }

    class CurrencyTextWatcher(private var editText: EditText) : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            editText.removeTextChangedListener(this)

            try {
                var originalString = s.toString()
                if (originalString.contains(".")) {
                    //Replace from 8.000.000,80 to 8000000,80
                    originalString = originalString.replace(".", "")
                    //Replace from 8000000,80 to 8000000.80
                    originalString = originalString.replace(",", ".")
                }
                val longval = originalString.toLong()
                val formatter: DecimalFormat = NumberFormat.getInstance(Locale.US) as DecimalFormat
                formatter.applyPattern("#,###,###,###")
                var formattedString: String = formatter.format(longval)

                //Setting text after format to EditText
                //Replace from 8,000,000.80 to 8,000,000%80
                formattedString = formattedString.replace(".", "%")
                //Replace from 8,000,000%80 to 8.000.000%80
                formattedString = formattedString.replace(",", ".")
                //Replace from 8.000.000%80 to 8.000.000,80
                formattedString = formattedString.replace("%", ",")
                editText.setText(formattedString)
                editText.setSelection(editText.text.length)
            } catch (nfe: NumberFormatException) {
                nfe.printStackTrace()
            }

            editText.addTextChangedListener(this)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    }
}