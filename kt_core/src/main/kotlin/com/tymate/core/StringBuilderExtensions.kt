package com.tymate.core

import java.text.Normalizer


fun StringBuilder.appendLine(line: String): Boolean = appendIfNotEmpty(line, "\n")

fun StringBuilder.appendIfNotEmpty(string: String?, addFirstIfNotEmpty: String? = null): Boolean {
    if (string.isNullOrBlank()) return false
    if (!isEmpty()) {
        append(addFirstIfNotEmpty)
    }
    append(string)
    return true
}

private val REGEX_UNACCENT = "\\p{InCombiningDiacriticalMarks}+".toRegex()

fun CharSequence.unaccent(): String {
    val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
    return REGEX_UNACCENT.replace(temp, "")
}