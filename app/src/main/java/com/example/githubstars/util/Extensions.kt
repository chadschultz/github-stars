package com.example.githubstars.util

import android.icu.text.CompactDecimalFormat
import android.icu.util.ULocale
import android.os.Build
import androidx.annotation.RequiresApi

@delegate:RequiresApi(api = Build.VERSION_CODES.N)
private val compactDecimalFormat: CompactDecimalFormat by lazy {
    CompactDecimalFormat.getInstance(ULocale.US, CompactDecimalFormat.CompactStyle.SHORT)
}

fun Int.toCompactString(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        compactDecimalFormat.format(this)
    } else {
        this.toString()
    }
}