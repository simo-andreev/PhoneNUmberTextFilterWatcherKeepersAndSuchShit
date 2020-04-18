package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.util.Half.min
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.StringBuilder
import java.lang.ref.WeakReference
import kotlin.math.min

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView.addTextChangedListener(PhoneNumberEditor(textView))
        editText.filters += PhoneNumberFormatKeeper
        button.setOnClickListener { textView.text = editText.text }

    }
}

object PhoneNumberFormatKeeper : InputFilter {
    override fun filter(new: CharSequence, startIndexNew: Int, endIndexNew: Int, current: Spanned, startIndexCurrent: Int, endIndexCurrent: Int): CharSequence {
        val replacementSegment = new.subSequence(startIndexNew, endIndexNew)

        val defaultResult = current.replaceRange(startIndexCurrent, endIndexCurrent, replacementSegment)

        return if (!defaultResult.startsWith("(0)"))
            current.subSequence(startIndexCurrent, endIndexCurrent)
        else
            replacementSegment
    }

}

class PhoneNumberEditor(view: TextView) : TextWatcher {
    private val viewRef = WeakReference(view)
    private var lastResult = ""

    override fun afterTextChanged(text: Editable) {
        if (viewRef.get() == null) return

        if (text.isEmpty() || text.toString() == lastResult) return

        check(text.startsWith("(0)")) { "Phone numbers MUST start with (0)! Got [$text]." }

        val digits = text.replaceRange(0..2, "")
        val digitCount = digits.length

        val resultBuilder = StringBuilder("(0)${digits.subSequence(0, min(3, digitCount))}")
        if (digitCount > 3) {
            resultBuilder.append(" ")
            resultBuilder.append(digits.subSequence(3, min(5, digitCount)))
        }

        if (digitCount > 5) {
            resultBuilder.append("-")
            resultBuilder.append(digits.subSequence(5, min(7, digitCount)))
        }

        if (digitCount > 7) {
            resultBuilder.append("-")
            resultBuilder.append(digits.subSequence(7, min(9, digitCount)))
        }

        lastResult = resultBuilder.toString()
        viewRef.get()!!.text = resultBuilder
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        /* no-op */
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        /* no-op */
    }
}
