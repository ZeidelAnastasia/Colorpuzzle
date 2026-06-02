package com.example.colorpuzzle.utils

import androidx.compose.ui.graphics.Color

fun colorDistance(
    c1: FloatArray,
    c2: FloatArray
): Float {

    val hueDiff = minOf(
        kotlin.math.abs(c1[0] - c2[0]),
        360f - kotlin.math.abs(c1[0] - c2[0])
    )

    val satDiff =
        kotlin.math.abs(c1[1] - c2[1]) * 100f

    val valueDiff =
        kotlin.math.abs(c1[2] - c2[2]) * 150f

    return hueDiff + satDiff + valueDiff
}

fun lerpHSV(
    start: FloatArray,
    end: FloatArray,
    t: Float
): FloatArray {

    var h1 = start[0]
    var h2 = end[0]

    if (kotlin.math.abs(h2 - h1) > 180f) {
        if (h1 > h2) {
            h2 += 360f
        } else {
            h1 += 360f
        }
    }

    val hue =
        (h1 + (h2 - h1) * t) % 360f

    val saturation =
        start[1] + (end[1] - start[1]) * t

    val value =
        start[2] + (end[2] - start[2]) * t

    return floatArrayOf(
        hue,
        saturation,
        value
    )
}

fun Color.toHue(): Float {

    val r = red
    val g = green
    val b = blue

    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)

    val delta = max - min

    if (delta == 0f) return 0f

    val hue = when (max) {
        r -> ((g - b) / delta) % 6f
        g -> ((b - r) / delta) + 2f
        else -> ((r - g) / delta) + 4f
    }

    return (hue * 60f + 360f) % 360f
}