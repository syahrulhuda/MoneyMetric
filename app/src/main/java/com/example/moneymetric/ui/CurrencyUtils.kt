package com.example.moneymetric.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.NumberFormat
import java.util.Locale

class CurrencyAmountInputVisualTransformation(
    private val fixedCursorAtTheEnd: Boolean = true
) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val inputText = text.text
        if (inputText.isEmpty()) return TransformedText(text, OffsetMapping.Identity)

        val intValue = inputText.toLongOrNull() ?: return TransformedText(text, OffsetMapping.Identity)

        // Format ke Rupiah (tanpa simbol Rp dan ,00 agar bersih di input)
        val formatted = NumberFormat.getNumberInstance(Locale("id", "ID")).format(intValue)

        val newText = AnnotatedString("Rp $formatted")

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (inputText.isEmpty()) return 0
                if (fixedCursorAtTheEnd) return newText.length
                // Logic sederhana: kursor selalu di akhir agar tidak pusing
                return newText.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                return inputText.length
            }
        }

        return TransformedText(newText, offsetMapping)
    }
}