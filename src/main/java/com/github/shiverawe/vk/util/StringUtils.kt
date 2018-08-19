package com.github.shiverawe.vk.util

fun String.parseList(): List<String> = split(",").map { it.trim() }

fun String.parseInts(): List<Int> = parseList().map { it.toInt() }
