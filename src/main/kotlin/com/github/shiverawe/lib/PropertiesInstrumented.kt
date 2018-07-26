package com.github.shiverawe.lib

import java.util.*

/**
 * This class provides easy way to load properties from file.
 */
open class PropertiesInstrumented(filename: String) {

    private val properties: Properties = Properties()

    init {
        this.javaClass.getResourceAsStream(filename).use {
            properties.load(it)
        }
    }

    protected fun property(name: String): String {
        return properties.getProperty(name)
    }
}