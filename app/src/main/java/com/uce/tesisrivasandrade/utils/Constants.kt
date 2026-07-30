package com.uce.tesisrivasandrade.utils

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.content.ContextCompat
import com.uce.tesisrivasandrade.R

object Constants {
    const val BASE_URL = "http://10.34.225.74:8080/"
    const val KEYCLOAK_BASE_URL = "https://fing-auth.ideasybits.com/auth/"
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
