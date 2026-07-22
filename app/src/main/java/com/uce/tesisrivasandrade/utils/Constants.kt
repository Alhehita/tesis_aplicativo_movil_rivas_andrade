package com.uce.tesisrivasandrade.utils

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.content.ContextCompat
import com.uce.tesisrivasandrade.R

object Constants {
    const val BASE_URL = "http://192.168.1.36:8080/"
}

/**
 * Configura un AutoCompleteTextView con un ArrayAdapter y color de texto negro.
 */
fun configurarDropdown(
    context: Context,
    autoComplete: AutoCompleteTextView,
    items: List<String>
) {
    val adapter = ArrayAdapter(
        context,
        R.layout.item_dropdown,
        items
    )
    autoComplete.setAdapter(adapter)
    autoComplete.setTextColor(
        ContextCompat.getColor(context, android.R.color.black)
    )
}
