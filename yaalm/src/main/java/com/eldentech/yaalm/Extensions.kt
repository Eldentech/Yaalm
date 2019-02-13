package com.eldentech.yaalm

fun String.isInValidList(vararg validList: String): Boolean  = validList.contains(this)

fun Int.isInValidList(vararg validList: Int): Boolean = validList.contains(this)

fun Int.isInValidRange(min: Int, max: Int): Boolean  = this in IntRange(min,max-1)

fun Long.isInValidRange(min: Long, max: Long): Boolean =  this in LongRange(min,max-1)