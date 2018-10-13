package com.eldentech.yaalm

import android.text.TextUtils

fun String.isInValidList(vararg validList: String): Boolean {
    for (item in validList) {
        if(TextUtils.equals(this, item))
            return true
    }
    return false
}

fun Int.isInValidList(vararg validList: Int): Boolean {
    for (item in validList) {
        if(this == item)
            return true
    }
    return false
}

fun Int.isInValidRange(min: Int, max: Int): Boolean {
    return this in (min + 1)..(max - 1)
}

fun Long.isInValidRange(min: Long, max: Long): Boolean {
    return this in (min + 1)..(max - 1)
}