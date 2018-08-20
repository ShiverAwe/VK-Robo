package com.github.shiverawe.vk.util

fun String.parseList(): List<String> = split(",").map { it.trim() }.filter { !it.isBlank() }

fun String.parseInts(): List<Int> = parseList().map { it.toInt() }
